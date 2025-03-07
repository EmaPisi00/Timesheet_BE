package it.project.timesheet.service;

import io.micrometer.common.util.StringUtils;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.BadRequestException;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.exception.custom.ObjectNotFoundException;
import it.project.timesheet.repository.EmployeeRepository;
import it.project.timesheet.repository.PresenceRepository;
import it.project.timesheet.service.base.EmployeeService;
import it.project.timesheet.service.base.PresenceService;
import it.project.timesheet.service.base.TimesheetService;
import it.project.timesheet.service.base.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PresenceServiceImpl implements PresenceService {

    private final PresenceRepository presenceRepository;
    private final UserService userService;
    private final EmployeeService employeeService;
    private final TimesheetService timesheetService;

    @Override
    public Presence save(Presence presence) throws BaseException {
        if (presence.getUuid() != null && StringUtils.isNotBlank(presence.getUuid().toString())) {
            throw new BadRequestException("UUID presente :  " + presence.getUuid().toString());
        }

        Timesheet timesheet = timesheetService.findByUuid(presence.getTimesheet().getUuid());
        Employee employee = employeeService.findByUuid(timesheet.getEmployee().getUuid());
        userService.findByUuid(employee.getUser().getUuid());

        if (presence.getWorkDay() == null
                || presence.getEntryTime() == null
                || presence.getExitTime() == null
                || StringUtils.isBlank(presence.getDescription())
                || presence.getStatusDayEnum() == null
                || presence.getStatusHoursEnum() == null
                || presence.getTotalHours() == null) {
            throw new BadRequestException("Giorno lavorativo | orario entrata | orario uscita | descrizione | status giorno | status ore | totale ore non inseriti");
        }

        presence.setTimesheet(timesheet);

        return persistOnMysql(presence);
    }

    @Override
    public Presence findByUuid(UUID uuid) throws BaseException {
        Presence presence = presenceRepository.findByUuidAndDeletedAtIsNull(uuid).
                orElseThrow(() -> new ObjectNotFoundException("Presence non trovata con questo UUID: " + uuid.toString()));
        log.info("Presence trovata {}", presence);
        return presence;
    }

    @Override
    public Presence updateByUuid(Presence presence, UUID uuid) throws BaseException {
        Presence presenceFound = findByUuid(uuid);
        Timesheet timesheet = timesheetService.findByUuid(presence.getTimesheet().getUuid());
        Employee employee = employeeService.findByUuid(timesheet.getEmployee().getUuid());
        userService.findByUuid(employee.getUser().getUuid());

        if (presence.getWorkDay() == null
                || presence.getEntryTime() == null
                || presence.getExitTime() == null
                || StringUtils.isBlank(presence.getDescription())) {
            throw new BadRequestException("Giorno lavorativo | orario entrata | orario uscita | descrizione non inseriti");
        }

        presenceFound.setWorkDay(presence.getWorkDay());
        presenceFound.setEntryTime(presence.getEntryTime());
        presenceFound.setExitTime(presence.getExitTime());
        presenceFound.setDescription(presence.getDescription());
        presenceFound.setStatusDayEnum(presence.getStatusDayEnum());
        presenceFound.setStatusHoursEnum(presence.getStatusHoursEnum());
        presenceFound.setTotalHours(presence.getTotalHours());
        presenceFound.setTimesheet(timesheet);

        return persistOnMysql(presenceFound);
    }

    @Override
    public void deleteByUuid(UUID uuid) throws BaseException {
        Presence presence = findByUuid(uuid);
        presence.deleted();
        log.info("Presence eliminato (logicamente) {}", presence);

        // prevedere un metodo di Facade che quando elimino logicamente un utente questo deve
        // cancellare logicamente a cascata in tutte le tabelle in cui viene richiamato.
    }

    @Override
    public List<Presence> findAll() {
        return presenceRepository.findAllByDeletedAtIsNull();
    }

    @Override
    public List<Presence> saveAll(List<Presence> presences) {
        return presences.stream().map(this::persistOnMysql).toList();
    }

    private Presence persistOnMysql(Presence presence) {
        presence = presenceRepository.save(presence);
        log.info("Presence salvato {}", presence);
        return presence;
    }
}
