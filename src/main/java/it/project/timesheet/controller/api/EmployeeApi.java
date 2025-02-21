package it.project.timesheet.controller.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/timesheet/empoloyee")
@Tag(name = "Employee")
public interface EmployeeApi {

    @PostMapping
    Employee save(@RequestBody Employee employee) throws BaseException;

    @GetMapping
    List<Employee> findAll();

    @GetMapping("/{uuid}")
    Employee findById(@PathVariable("uuid") UUID uuid) throws BaseException;

    @PutMapping("/{uuid}")
    Employee update(@PathVariable("uuid") UUID uuid, @RequestBody Employee employee) throws BaseException;

    @DeleteMapping("/{uuid}")
    void delete(@PathVariable("uuid") UUID uuid) throws BaseException;

}
