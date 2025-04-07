package it.project.timesheet.service.facade.base;

import it.project.timesheet.domain.dto.request.TimesheetRequestDto;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public interface TimesheetFacade {

    TimesheetRequestDto generateTimesheet(Integer month, Integer year, UUID uuidEmployee) throws BaseException;

    Timesheet saveTimesheet(TimesheetRequestDto timesheetRequestDto) throws BaseException;

    ByteArrayResource createExcelFileOutput(UUID uuid) throws BaseException, IOException;
}
