package it.project.timesheet.controller;

import it.project.timesheet.controller.api.UserApi;
import it.project.timesheet.domain.dto.UserDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.service.base.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;

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
}
