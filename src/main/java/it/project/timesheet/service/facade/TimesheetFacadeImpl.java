package it.project.timesheet.service.facade;

import it.project.timesheet.domain.dto.TimesheetDto;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.service.base.PresenceService;
import it.project.timesheet.service.base.TimesheetService;
import it.project.timesheet.service.facade.base.TimesheetFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TimesheetFacadeImpl implements TimesheetFacade {

    private final TimesheetService timesheetService;
    private final PresenceService presenceService;

    @Override
    public TimesheetDto generateTimesheet(Integer month, Integer year, UUID uuidEmployee) throws BaseException {

        // Ricerca per mese e anno per vedere se esiste già un timesheet con quel mese ed anno
        Timesheet timesheet = timesheetService.findByMonthAndYearAndEmployee(month, year, uuidEmployee);




        return null;
    }
}
