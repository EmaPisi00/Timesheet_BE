package it.project.timesheet.service.auth;

import io.jsonwebtoken.Claims;
import it.project.timesheet.configuration.JwtTokenConfiguration;
import it.project.timesheet.domain.dto.request.UserRequestDto;
import it.project.timesheet.domain.dto.response.AuthResponseDto;
import it.project.timesheet.domain.dto.response.UserResponseDto;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.domain.enums.RoleEnum;
import it.project.timesheet.exception.UnauthorizedException;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.exception.custom.ObjectNotFoundException;
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

    public User register(UserRequestDto userRequestDto) throws BaseException {
        User user = new User();
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return userService.save(user);
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
            userResponseDto.setEmail(user.getEmail());
            userResponseDto.setUuidUser(user.getUuid());
            userResponseDto.setRole(RoleEnum.fromString(user.getRole()));
            //userResponseDto.setPassword(user.getPassword());

            Employee employee = employeeService.findByUser(user.getUuid());
            userResponseDto.setName(employee.getName());
            userResponseDto.setSurname(employee.getSurname());
            userResponseDto.setUuidEmployee(employee.getUuid());
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

    private String getTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }

}
