package it.project.timesheet.controller.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import it.project.timesheet.domain.dto.request.UserRequestDto;
import it.project.timesheet.domain.dto.response.AuthResponseDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public interface UserApi {

    @GetMapping
    List<User> findAll();

    @GetMapping("/{uuid}")
    User findById(@PathVariable("uuid") UUID uuid) throws BaseException;

    @DeleteMapping("/{uuid}")
    void delete(@PathVariable("uuid") UUID uuid) throws BaseException;

    /**
     * AUTH API
     **/

    @PostMapping("/register")
    User register(@RequestBody UserRequestDto userRequestDto) throws BaseException;

    @PostMapping("/login")
    AuthResponseDto login(@RequestBody UserRequestDto userRequestDto) throws BaseException;

    @GetMapping("/verify")
    boolean verify(@RequestParam("token") String token);
}
