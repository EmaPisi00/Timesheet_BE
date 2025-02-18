package it.project.timesheet.controller.api;


import it.project.timesheet.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/timesheet/user")
public interface UserApi {

    @GetMapping("/")
    List<User> findAll();

}
