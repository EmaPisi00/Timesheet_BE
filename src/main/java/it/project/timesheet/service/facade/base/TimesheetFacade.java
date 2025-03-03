package it.project.timesheet.service.facade.base;

import it.project.timesheet.domain.dto.RequestTimesheetDto;
import it.project.timesheet.domain.dto.TimesheetDto;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TimesheetFacade {

    RequestTimesheetDto generateTimesheet(Integer month, Integer year, UUID uuidEmployee) throws BaseException;

}
