package it.project.timesheet.controller.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/presence")
@Tag(name = "Presence")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
public interface PresenceApi {

    @PostMapping
    Presence save(@RequestBody Presence presence) throws BaseException;

    @GetMapping
    List<Presence> findAll();

    @GetMapping("/{uuid}")
    Presence findById(@PathVariable("uuid") UUID uuid) throws BaseException;

    @PutMapping("/{uuid}")
    Presence update(@PathVariable("uuid") UUID uuid, @RequestBody Presence presence) throws BaseException;

    @DeleteMapping("/{uuid}")
    void delete(@PathVariable("uuid") UUID uuid) throws BaseException;

}
