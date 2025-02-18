package it.project.timesheet.controller;

import it.project.timesheet.controller.api.UserApi;
import it.project.timesheet.entity.User;
import it.project.timesheet.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    public List<User> findAll() {
        return userService.findAll();
    }
}
