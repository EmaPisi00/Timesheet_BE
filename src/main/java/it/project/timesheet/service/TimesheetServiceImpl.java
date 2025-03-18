package it.project.timesheet.service;

import io.micrometer.common.util.StringUtils;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.exception.BadRequestException;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.exception.custom.ObjectNotFoundException;
import it.project.timesheet.repository.TimesheetRepository;
import it.project.timesheet.service.base.EmployeeService;
import it.project.timesheet.service.base.TimesheetService;
import it.project.timesheet.utils.DateUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final EmployeeService employeeService;

    @Override
    public Timesheet save(Timesheet timesheet) throws BaseException {
        if (timesheet.getUuid() != null && StringUtils.isNotBlank(timesheet.getUuid().toString())) {
            throw new BadRequestException("UUID presente :  " + timesheet.getUuid().toString());
        }

        Employee employee = employeeService.findByUuid(timesheet.getEmployee().getUuid());

        if (timesheet.getYear() == null || timesheet.getMonth() == null) {
            throw new BadRequestException("Mese o anno non inseriti");
        }

        timesheet.setEmployee(employee);

        return persistOnMysql(timesheet);
    }

    @Override
    public Timesheet findByUuid(UUID uuid) throws BaseException {
        Timesheet timesheet = timesheetRepository.findByUuidAndDeletedAtIsNull(uuid).
                orElseThrow(() -> new ObjectNotFoundException("Timesheet non trovato con questo UUID: " + uuid.toString()));
        log.info("Timesheet trovato {}", timesheet);
        return timesheet;
    }

    @Override
    public Timesheet updateByUuid(Timesheet timesheet, UUID uuid) throws BaseException {
        Timesheet timesheetFound = findByUuid(uuid);
        Employee employeeFound = employeeService.findByUuid(timesheet.getEmployee().getUuid());

        if (timesheet.getYear() == null || timesheet.getMonth() == null) {
            throw new BadRequestException("Mese o anno non inseriti");
        }

        timesheetFound.setYear(timesheet.getYear());
        timesheetFound.setMonth(timesheet.getMonth());
        timesheetFound.setEmployee(employeeFound);

        return persistOnMysql(timesheetFound);
    }

    @Override
    public void deleteByUuid(UUID uuid) throws BaseException {
        Timesheet timesheet = findByUuid(uuid);
        timesheet.deleted();
        log.info("Timesheet eliminato (logicamente) {}", timesheet);

        // prevedere un metodo di Facade che quando elimino logicamente un utente questo deve
        // cancellare logicamente a cascata in tutte le tabelle in cui viene richiamato.
    }

    @Override
    public List<Timesheet> findAll() {
        return timesheetRepository.findAllByDeletedAtIsNull();
    }

    @Override
    public Timesheet findByMonthAndYearAndEmployee(Integer month, Integer year, UUID uuidEmployee) throws BaseException {
        Employee employee = employeeService.findByUuid(uuidEmployee);
        Timesheet timesheet = timesheetRepository.findByMonthAndYearAndEmployeeAndLockedIsFalseAndDeletedAtIsNull(month, year, employee).
                orElseThrow(() -> new ObjectNotFoundException("Timesheet non trovato con questo mese: " +
                        DateUtils.getMonthName(month) + " e questo anno : " + year +
                        " per questo uuidEmployee : " + uuidEmployee));
        log.info("Timesheet trovato {}", timesheet);
        return timesheet;
    }

    @Override
    public boolean existsTimesheetForMonthAndYearAndEmployeeAndLockedIsFalse(Integer month, Integer year, Employee employee) {
        return timesheetRepository.findByMonthAndYearAndEmployeeAndLockedIsFalseAndDeletedAtIsNull(month, year, employee).isPresent(); // Restituisce true se trovato, false se non trovato
    }

    @Override
    public boolean existsTimesheetForMonthAndYearAndEmployeeAndLockedIsTrue(Integer month, Integer year, UUID uuidEmployee) throws BaseException {
        Employee employee = employeeService.findByUuid(uuidEmployee);
        return timesheetRepository.findByMonthAndYearAndEmployeeAndLockedIsTrueAndDeletedAtIsNull(month, year, employee).isPresent(); // Restituisce true se trovato, false se non trovato
    }

    private Timesheet persistOnMysql(Timesheet timesheet) {
        timesheet = timesheetRepository.save(timesheet);
        log.info("Timesheet salvato {}", timesheet);
        return timesheet;
    }
}
