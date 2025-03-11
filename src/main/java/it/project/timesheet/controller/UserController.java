package it.project.timesheet.controller;

import it.project.timesheet.controller.api.UserApi;
import it.project.timesheet.domain.dto.request.UserRequestDto;
import it.project.timesheet.domain.dto.response.AuthResponseDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.service.auth.AuthService;
import it.project.timesheet.service.base.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;
    private final AuthService authService;


    @Override
    public List<User> findAll() {
        return userService.findAll();
    }

    @Override
    public User findById(UUID uuid) throws BaseException {
        return userService.findByUuid(uuid);
    }

    @Override
    public void delete(UUID uuid) throws BaseException {
        userService.deleteByUuid(uuid);
    }

    @Override
    public User register(UserRequestDto userRequestDto) throws BaseException {
        return authService.register(userRequestDto);
    }

    @Override
    public AuthResponseDto login(UserRequestDto userRequestDto) throws BaseException {
        return authService.login(userRequestDto);
    }

    @Override
    public boolean verify(String token) {
        return false;
    }
}
