package it.project.timesheet.service.auth;

import io.micrometer.common.util.StringUtils;
import it.project.timesheet.configuration.JwtTokenConfiguration;
import it.project.timesheet.domain.dto.request.EmployeeRequestDto;
import it.project.timesheet.domain.dto.request.ResetPasswordRequestDto;
import it.project.timesheet.domain.dto.request.UserRequestDto;
import it.project.timesheet.domain.dto.response.AuthResponseDto;
import it.project.timesheet.domain.dto.response.UserResponseDto;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.BadRequestException;
import it.project.timesheet.exception.UnauthorizedException;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.exception.custom.InconsistencyDatetimeException;
import it.project.timesheet.exception.custom.ObjectNotFoundException;
import it.project.timesheet.mapper.UserMapper;
import it.project.timesheet.service.base.EmployeeService;
import it.project.timesheet.service.base.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationProvider authenticationProvider;
    private final JwtTokenConfiguration jwtTokenConfiguration;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailService userDetailService;
    private final UserService userService;
    private final EmployeeService employeeService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthResponseDto login(UserRequestDto userRequestDto) {
        try {
            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRequestDto.getEmail(),
                            userRequestDto.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return new AuthResponseDto(jwtTokenConfiguration.generateToken(userDetails));
        } catch (BadCredentialsException e) {
            throw new UsernameNotFoundException("Credenziali non valide");
        } catch (DisabledException e) {
            throw new UsernameNotFoundException("Account disabilitato");
        }
    }

    public Employee register(EmployeeRequestDto employeeRequestDto) throws BaseException {

        if (StringUtils.isBlank(employeeRequestDto.getEmail()) || StringUtils.isBlank(employeeRequestDto.getPassword())
                || StringUtils.isBlank(employeeRequestDto.getName()) || StringUtils.isBlank(employeeRequestDto.getSurname())) {
            throw new BadRequestException("Email|Password|Name|Surname non valide");
        }

        User user = userService.save(User.builder()
                .email(employeeRequestDto.getEmail())
                .password(passwordEncoder.encode(employeeRequestDto.getPassword()))
                .build());

        return employeeService.save(Employee.builder()
                .user(user)
                .name(employeeRequestDto.getName())
                .surname(employeeRequestDto.getSurname())
                .build());
    }

    public boolean validateToken(String token) {
        return jwtTokenConfiguration.validateToken(getTokenFromHeader(token));
    }

    public UserResponseDto getUserProfile(String token) throws BaseException {
        UserResponseDto userResponseDto = new UserResponseDto();

        // Verifico se il token è valido
        boolean validToken = validateToken(token);

        // Se il token è valido restituisco recupero le informazioni
        if (validToken) {
            String email = jwtTokenConfiguration.extractUsername(getTokenFromHeader(token));

            User user = userService.findByEmail(email).orElseThrow(() -> new ObjectNotFoundException("Utente non trovato con questa email: "
                    + email));
            Employee employee = employeeService.findByUser(user.getUuid());
            userResponseDto = UserMapper.INSTANCE.convertUserToUserDto(user, employee);
        }

        return userResponseDto;
    }


    public AuthResponseDto refreshToken(String token) throws BaseException {
        AuthResponseDto authResponseDto = new AuthResponseDto();
        try {
            String cleanToken = getTokenFromHeader(token);
            String username = jwtTokenConfiguration.extractUsername(cleanToken);

            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            String newToken = jwtTokenConfiguration.refreshToken(cleanToken, userDetails);

            authResponseDto.setToken(newToken);
            return authResponseDto;
        } catch (Exception e) {
            throw new UnauthorizedException("Token non valido o non rinnovabile");
        }
    }

    public void logout(String token) {
        String jwt = token.replace("Bearer ", "");
        tokenBlacklistService.blacklistToken(jwt);
    }

    public UserResponseDto resetPassword(String token, ResetPasswordRequestDto resetPasswordRequestDto) throws BaseException {
        String email = jwtTokenConfiguration.extractUsername(getTokenFromHeader(token));
        UserResponseDto userResponseDto = new UserResponseDto();

        if (StringUtils.isNotBlank(email)) {
            User user = userService.findByEmail(email).orElse(null);

            if (user != null) {
                if (user.getResetPassword() == null || user.getResetPassword().isBefore(LocalDateTime.now())) {

                    String password = resetPasswordRequestDto.getPassword();
                    String repeatPassword = resetPasswordRequestDto.getRepeatPassword();

                    if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(repeatPassword)) {
                        if (!password.equals(repeatPassword)) {
                            throw new BadRequestException("Le password non coincidono.");
                        }

                        // REGEX: almeno 8 caratteri, 1 maiuscola, 1 minuscola, 1 cifra, 1 speciale
                        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

                        if (!password.matches(passwordRegex)) {
                            throw new BadRequestException("La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale.");
                        }

                        user.setPassword(passwordEncoder.encode(password));

                        // Imposta la finestra per un altro reset a 30 minuti da ora
                        user.setResetPassword(LocalDateTime.now().plusMinutes(30));

                        Employee employee = employeeService.findByUser(user.getUuid());
                        userResponseDto = UserMapper.INSTANCE.convertUserToUserDto(userService.updateByUuid(user, user.getUuid()), employee);
                    } else {
                        throw new IllegalArgumentException("Password e conferma non possono essere vuote.");
                    }
                } else {
                    throw new InconsistencyDatetimeException("Impossibile cambiare la password: richiesta già effettuata di recente.");
                }
            }
        }
        return userResponseDto;
    }


    private String getTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }

}
