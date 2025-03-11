package it.project.timesheet.controller.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import it.project.timesheet.domain.dto.UserDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public interface UserApi {

    @PostMapping
    User save(@RequestBody UserDto userDto) throws BaseException;

    @GetMapping
    List<User> findAll();

    @GetMapping("/{uuid}")
    User findById(@PathVariable("uuid") UUID uuid) throws BaseException;

    @PutMapping("/{uuid}")
    User update(@PathVariable("uuid") UUID uuid, @RequestBody UserDto userDto) throws BaseException;

    @DeleteMapping("/{uuid}")
    void delete(@PathVariable("uuid") UUID uuid) throws BaseException;

    @PostMapping("/login")
    String login(@RequestBody UserDto userDto) throws BaseException;
}
