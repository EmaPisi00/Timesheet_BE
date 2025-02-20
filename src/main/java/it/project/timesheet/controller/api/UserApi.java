package it.project.timesheet.controller.api;


import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timesheet/user")
public interface UserApi {

    @PostMapping
    User save(@RequestBody User user) throws BaseException;

    @GetMapping("/")
    List<User> findAll();

}
