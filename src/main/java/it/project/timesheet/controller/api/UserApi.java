package it.project.timesheet.controller.api;


import it.project.timesheet.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timesheet/user")
public interface UserApi {

    @PostMapping("/")
    User save(@RequestBody User user);

    @GetMapping("/")
    List<User> findAll();

}
