package it.project.timesheet.controller.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employee")
@Tag(name = "Employee")
public interface EmployeeApi {

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Employee save(@RequestBody Employee employee) throws BaseException;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    List<Employee> findAll();

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    Employee findById(@PathVariable("uuid") UUID uuid) throws BaseException;

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    Employee update(@PathVariable("uuid") UUID uuid, @RequestBody Employee employee) throws BaseException;

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void delete(@PathVariable("uuid") UUID uuid) throws BaseException;

}
