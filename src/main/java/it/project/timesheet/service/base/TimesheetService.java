package it.project.timesheet.service.base;

import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TimesheetService {

    Timesheet save(Timesheet timesheet) throws BaseException;

    Timesheet findByUuid(UUID uuid) throws BaseException;

    Timesheet updateByUuid(Timesheet timesheet, UUID uuid) throws BaseException;

    void deleteByUuid(UUID uuid) throws BaseException;

    List<Timesheet> findAll();
}
