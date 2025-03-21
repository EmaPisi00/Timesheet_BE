package it.project.timesheet.controller.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import it.project.timesheet.domain.dto.request.TimesheetRequestDto;
import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/timesheet")
@Tag(name = "Timesheet")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
public interface TimesheetApi {

    @GetMapping
    List<Timesheet> findAll();

    @GetMapping("/{uuid}")
    Timesheet findById(@PathVariable("uuid") UUID uuid) throws BaseException;

    @PutMapping("/{uuid}")
    Timesheet update(@PathVariable("uuid") UUID uuid, @RequestBody Timesheet timesheet) throws BaseException;

    @DeleteMapping("/{uuid}")
    void delete(@PathVariable("uuid") UUID uuid) throws BaseException;

    // Other Api

    @GetMapping("/findByMonthAndYearAndEmployee/month/{month}/year/{year}/uuidEmployee/{uuid_employee}")
    Timesheet findByMonthAndYearAndEmployee(@PathVariable("month") Integer month, @PathVariable("year") Integer year, @PathVariable("uuid_employee") UUID uuidEmployee) throws BaseException;

    @GetMapping("/generateTimesheet/month/{month}/year/{year}/uuidEmployee/{uuid_employee}")
    TimesheetRequestDto generateTimesheet(@PathVariable("month") Integer month, @PathVariable("year") Integer year, @PathVariable("uuid_employee") UUID uuidEmployee) throws BaseException;

    @PostMapping("/saveTimesheet")
    Timesheet saveTimesheet(@RequestBody TimesheetRequestDto timesheetRequestDto) throws BaseException;

    @GetMapping("/findAllByEmployee/{uuid_employee}")
    Page<Timesheet> findAllByEmployee(@PathVariable("uuid_employee") UUID uuidEmployee, Pageable pageable) throws BaseException;
}
