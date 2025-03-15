package it.project.timesheet.controller.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import it.project.timesheet.domain.dto.request.UserRequestDto;
import it.project.timesheet.domain.dto.response.AuthResponseDto;
import it.project.timesheet.domain.dto.response.UserResponseDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public interface UserApi {

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    List<User> findAll();

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    User findById(@PathVariable("uuid") UUID uuid) throws BaseException;

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void delete(@PathVariable("uuid") UUID uuid) throws BaseException;

    /**
     * AUTH API
     **/

    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    User register(@RequestBody UserRequestDto userRequestDto) throws BaseException;

    @PostMapping("/login")
    AuthResponseDto login(@RequestBody UserRequestDto userRequestDto) throws BaseException;

    @PostMapping("/verify")
    boolean verify(@RequestHeader("Authorization") String token);

    @GetMapping("/getProfile")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    UserResponseDto getUserProfile(@RequestHeader("Authorization") String token) throws BaseException;

    @PostMapping("/refreshToken")
    AuthResponseDto refreshToken(@RequestHeader("Authorization") String token) throws BaseException;

}
