package it.project.timesheet.service.base;

import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface PresenceService {

    Presence save(Presence presence) throws BaseException;

    Presence findByUuid(UUID uuid) throws BaseException;

    Presence updateByUuid(Presence presence, UUID uuid) throws BaseException;

    void deleteByUuid(UUID uuid) throws BaseException;

    List<Presence> findAll();
}
