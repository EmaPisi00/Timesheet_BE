package it.project.timesheet.controller;

import it.project.timesheet.controller.api.TimesheetApi;
import it.project.timesheet.domain.dto.request.TimesheetRequestDto;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.service.base.TimesheetService;
import it.project.timesheet.service.facade.base.TimesheetFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TimesheetController implements TimesheetApi {
    private final TimesheetService timesheetService;
    private final TimesheetFacade timesheetFacade;

    @Override
    public Page<Timesheet> findAll(Pageable pageable) {
        return timesheetService.findAll(pageable);
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
    public Timesheet findByMonthAndYearAndEmployee(Integer month, Integer year, UUID uuidEmployee) throws BaseException {
        return timesheetService.findByMonthAndYearAndEmployee(month, year, uuidEmployee);
    }

    @Override
    public TimesheetRequestDto generateTimesheet(Integer month, Integer year, UUID uuidEmployee) throws BaseException {
        return timesheetFacade.generateTimesheet(month, year, uuidEmployee);
    }

    @Override
    public Timesheet saveTimesheet(TimesheetRequestDto timesheetRequestDto) throws BaseException {
        return timesheetFacade.saveTimesheet(timesheetRequestDto);
    }

    @Override
    public Page<Timesheet> findAllByEmployee(UUID uuidEmployee, Pageable pageable) throws BaseException {
        return timesheetService.findAllTimesheetsByEmployee(pageable, uuidEmployee);
    }

    @Override
    public Timesheet blockTimesheet(UUID uuid) throws BaseException {
        return timesheetService.blockTimesheet(uuid);
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadExcel(UUID uuid) throws BaseException, IOException {
        ByteArrayResource fileResource = timesheetFacade.createExcelFileOutput(uuid);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=OrarioLavorativo.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileResource.contentLength())
                .body(fileResource);
    }
}
