package it.project.timesheet.controller;

import it.project.timesheet.configuration.JwtTokenConfiguration;
import it.project.timesheet.controller.api.UserApi;
import it.project.timesheet.domain.dto.UserDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.service.auth.AuthService;
import it.project.timesheet.service.base.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;
    private final AuthenticationProvider authenticationProvider;
    private final AuthService authService;
    private final JwtTokenConfiguration jwtUtil;

    @Override
    public User save(UserDto userDto) throws BaseException {
        return userService.save(userDto);
    }

    @Override
    public List<User> findAll() {
        return userService.findAll();
    }

    @Override
    public User findById(UUID uuid) throws BaseException {
        return userService.findByUuid(uuid);
    }

    @Override
    public User update(UUID uuid, UserDto userDto) throws BaseException {
        return userService.updateByUuid(userDto, uuid);
    }

    @Override
    public void delete(UUID uuid) throws BaseException {
        userService.deleteByUuid(uuid);
    }

    @Override
    public String login(UserDto userDto) throws BaseException {

        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getEmail(),
                        userDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = authService.loadUserByUsername(userDto.getEmail());
        return jwtUtil.generateToken(userDetails);
    }

    @Override
    public boolean verify(String token) throws BaseException {
        UserDetails userDetails = authService.loadUserByUsername("test1@gmail.com");
        return jwtUtil.validateToken(token, userDetails);
    }
}
