package it.project.timesheet.controller.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/timesheet")
@Tag(name = "Timesheet")
public interface TimesheetApi {

    @PostMapping
    Timesheet save(@RequestBody Timesheet timesheet) throws BaseException;

    @GetMapping
    List<Timesheet> findAll();

    @GetMapping("/{uuid}")
    Timesheet findById(@PathVariable("uuid") UUID uuid) throws BaseException;

    @PutMapping("/{uuid}")
    Timesheet update(@PathVariable("uuid") UUID uuid, @RequestBody Timesheet timesheet) throws BaseException;

    @DeleteMapping("/{uuid}")
    void delete(@PathVariable("uuid") UUID uuid) throws BaseException;

}
