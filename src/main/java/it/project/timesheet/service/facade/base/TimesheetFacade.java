package it.project.timesheet.service.facade.base;

import it.project.timesheet.domain.dto.request.TimesheetRequestDto;
import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TimesheetFacade {

    TimesheetRequestDto generateTimesheet(Integer month, Integer year, UUID uuidEmployee) throws BaseException;

    List<Presence> saveTimesheet(TimesheetRequestDto timesheetRequestDto) throws BaseException;

}
