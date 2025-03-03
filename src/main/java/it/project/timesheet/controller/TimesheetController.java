package it.project.timesheet.controller;

import it.project.timesheet.controller.api.TimesheetApi;
import it.project.timesheet.domain.dto.RequestTimesheetDto;
import it.project.timesheet.domain.dto.TimesheetDto;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.service.base.TimesheetService;
import it.project.timesheet.service.facade.base.TimesheetFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TimesheetController implements TimesheetApi {
    private final TimesheetService timesheetService;
    private final TimesheetFacade timesheetFacade;

    @Override
    public Timesheet save(Timesheet timesheet) throws BaseException {
        return timesheetService.save(timesheet);
    }

    @Override
    public List<Timesheet> findAll() {
        return timesheetService.findAll();
    }

    @Override
    public Timesheet findById(UUID uuid) throws BaseException {
        return timesheetService.findByUuid(uuid);
    }

    @Override
    public Timesheet update(UUID uuid, Timesheet timesheet) throws BaseException {
        return timesheetService.updateByUuid(timesheet, uuid);
    }

    @Override
    public void delete(UUID uuid) throws BaseException {
        timesheetService.deleteByUuid(uuid);
    }

    @Override
    public Timesheet findByMonthAndYear(Integer month, Integer year, UUID uuidEmployee) throws BaseException {
        return timesheetService.findByMonthAndYearAndEmployee(month, year,uuidEmployee);
    }

    @Override
    public RequestTimesheetDto generateTimesheet(Integer month, Integer year, UUID uuidEmployee) throws BaseException {
        return timesheetFacade.generateTimesheet(month, year, uuidEmployee);
    }
}
