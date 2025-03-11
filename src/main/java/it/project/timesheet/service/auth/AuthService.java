package it.project.timesheet.service.auth;

import it.project.timesheet.configuration.JwtTokenConfiguration;
import it.project.timesheet.domain.dto.request.UserRequestDto;
import it.project.timesheet.domain.dto.response.AuthResponseDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
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
    private final UserService userService;

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
        return jwtTokenConfiguration.validateToken(token);
    }

}
