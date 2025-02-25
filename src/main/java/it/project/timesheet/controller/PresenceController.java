package it.project.timesheet.controller;

import it.project.timesheet.controller.api.PresenceApi;
import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.service.base.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PresenceController implements PresenceApi {
    private final PresenceService presenceService;

    @Override
    public Presence save(Presence presence) throws BaseException {
        return presenceService.save(presence);
    }

    @Override
    public List<Presence> findAll() {
        return presenceService.findAll();
    }

    @Override
    public Presence findById(UUID uuid) throws BaseException {
        return presenceService.findByUuid(uuid);
    }

    @Override
    public Presence update(UUID uuid, Presence presence) throws BaseException {
        return presenceService.updateByUuid(presence, uuid);
    }

    @Override
    public void delete(UUID uuid) throws BaseException {
        presenceService.deleteByUuid(uuid);
    }
}
