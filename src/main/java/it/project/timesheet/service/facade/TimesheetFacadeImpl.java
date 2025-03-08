package it.project.timesheet.service.facade;

import it.project.timesheet.domain.dto.PresenceDto;
import it.project.timesheet.domain.dto.RequestTimesheetDto;
import it.project.timesheet.domain.dto.TimesheetDto;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.domain.enums.StatusDayEnum;
import it.project.timesheet.domain.enums.StatusHoursEnum;
import it.project.timesheet.exception.BadRequestException;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.exception.custom.InconsistencyDatetimeException;
import it.project.timesheet.exception.custom.ObjectFoundException;
import it.project.timesheet.service.base.EmployeeService;
import it.project.timesheet.service.base.PresenceService;
import it.project.timesheet.service.base.TimesheetService;
import it.project.timesheet.service.facade.base.TimesheetFacade;
import it.project.timesheet.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimesheetFacadeImpl implements TimesheetFacade {

    private final TimesheetService timesheetService;
    private final EmployeeService employeeService;
    private final PresenceService presenceService;

    @Override
    public RequestTimesheetDto generateTimesheet(Integer month, Integer year, UUID uuidEmployee) throws BaseException {
        Set<LocalDate> holidays = DateUtils.getHolidays(year);
        RequestTimesheetDto requestTimesheetDto = new RequestTimesheetDto();

        // Ricerca per mese e anno per vedere se esiste già un timesheet con quel mese ed anno
        if (timesheetService.existsTimesheetForMonthAndYearAndEmployee(month, year, uuidEmployee)) {
            throw new ObjectFoundException("Oggetto già presente a DB!");
        }

        // Creazione di un nuovo oggetto timesheet sulla base del mese, anno e uuidEmployee dati in input
        Employee employee = employeeService.findByUuid(uuidEmployee);
        TimesheetDto timesheetDto = TimesheetDto.builder()
                .year(year)
                .month(month)
                .user(employee.getUser())
                .name(employee.getName())
                .surname(employee.getSurname())
                .build();

        requestTimesheetDto.setTimesheetDto(timesheetDto);

        // Ciclo tutti i giorni del mese
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        List<PresenceDto> presenceList = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            PresenceDto presenceDto = new PresenceDto();

            // Creo un oggetto LocalDate del giorno corrente
            LocalDate date = LocalDate.of(year, month, day);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // Calcolo e controllo se il giorno corrente è un giorno festivo o un weekend
            boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
            boolean isHoliday = holidays.contains(date);

            LocalTime holidayEntryTime = LocalTime.of(0, 0);
            LocalTime holidayExitTime = LocalTime.of(0, 0);

            if (isWeekend || isHoliday) {
                presenceDto.setStatusDayEnum(StatusDayEnum.HOLIDAY);
            } else {
                presenceDto.setStatusDayEnum(StatusDayEnum.WORKDAY);
                holidayEntryTime = LocalTime.of(9, 0);
                holidayExitTime = LocalTime.of(18, 0);
            }

            presenceDto.setStatusHoursEnum(StatusHoursEnum.NORMAL_WORKING);
            presenceDto.setWorkDay(date);
            presenceDto.setEntryTime(holidayEntryTime);
            presenceDto.setExitTime(holidayExitTime);
            presenceList.add(presenceDto);
        }

        requestTimesheetDto.setPresenceList(presenceList);

        return requestTimesheetDto;
    }

    @Override
    @Transactional(rollbackFor = BaseException.class)
    public List<Presence> saveTimesheet(RequestTimesheetDto requestTimesheetDto) throws BaseException {
        if (requestTimesheetDto == null || requestTimesheetDto.getPresenceList() == null || requestTimesheetDto.getTimesheetDto() == null) {
            throw new IllegalArgumentException("RequestTimesheetDto o la lista delle presenze è null");
        }

        Integer yearRequest = requestTimesheetDto.getTimesheetDto().getYear();
        Integer monthRequest = requestTimesheetDto.getTimesheetDto().getMonth();
        Set<LocalDate> holidays = DateUtils.getHolidays(yearRequest);

        Employee employee = employeeService.findByUser(requestTimesheetDto.getTimesheetDto().getUser().getUuid());

        // Ricerca per mese e anno per vedere se esiste già un timesheet con quel mese ed anno
        if (timesheetService.existsTimesheetForMonthAndYearAndEmployee(monthRequest, yearRequest, employee.getUuid())) {
            throw new ObjectFoundException("Timesheet già esistente a DB con questo mese : " + DateUtils.getMonthName(monthRequest)
                    + "\nquesto anno : " + yearRequest + "\ne questo UUID Dipendente : "
                    + employee.getUuid().toString());
        }

        validatePresenceDates(requestTimesheetDto.getPresenceList(), yearRequest, monthRequest);

        Timesheet timesheet = Timesheet.builder()
                .year(yearRequest)
                .month(monthRequest)
                .employee(employee)
                .build();
        timesheetService.save(timesheet);

        List<Presence> presenceList = new ArrayList<>();
        for (PresenceDto ithPresenceDto : requestTimesheetDto.getPresenceList()) {
            LocalDate day = ithPresenceDto.getWorkDay();
            boolean isWeekend = (day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY);
            boolean isHoliday = holidays.contains(day);

            if (ithPresenceDto.isIllnessed() && ithPresenceDto.isSmartWorking()) {
                throw new BadRequestException("Errore sono settati a TRUE sia lo stato MALATTIA che SMART_WORKING");
            }

            Presence presence = createPresence(ithPresenceDto, day, isHoliday, isWeekend, timesheet);
            presenceList.add(presence);
        }

        return presenceService.saveAll(presenceList);
    }

    private Presence createPresence(PresenceDto dto, LocalDate day, boolean isHoliday, boolean isWeekend, Timesheet timesheet) {
        Presence presence = new Presence();
        presence.setWorkDay(day);
        presence.setEntryTime(defaultTime(dto.getEntryTime()));
        presence.setExitTime(defaultTime(dto.getExitTime()));

        // Determina lo status del giorno
        if (dto.isIllnessed()) {
            setZeroTime(presence);
            presence.setStatusDayEnum(StatusDayEnum.ILLNESS.toString());
        } else if (dto.isSmartWorking()) {
            presence.setStatusDayEnum(StatusDayEnum.SMART_WORKING.toString());
        } else if (isHoliday || isWeekend) {
            setZeroTime(presence);
            presence.setStatusDayEnum(StatusDayEnum.HOLIDAY.toString());
        }

        // Calcola le ore lavorate
        double totalHours = calculateWorkedHours(presence.getEntryTime(), presence.getExitTime());
        presence.setTotalHours(totalHours);

        // Imposta lo status della giornata se non già impostato
        if (presence.getStatusDayEnum() == null) {
            presence.setStatusDayEnum(StatusDayEnum.WORKDAY.toString());
        }

        // Determina lo status delle ore
        if (totalHours > 8) {
            presence.setStatusHoursEnum(StatusHoursEnum.EXTRAORDINARY.toString());
        } else if (totalHours >= 4 && totalHours < 8) {
            presence.setStatusHoursEnum(StatusHoursEnum.PERMISSION.toString());
        } else if (totalHours == 8 || totalHours == 0) {
            presence.setStatusHoursEnum(StatusHoursEnum.NORMAL_WORKING.toString());
        }

        presence.setTimesheet(timesheet);
        return presence;
    }

    // Metodo di utility per evitare ripetizioni
    private void setZeroTime(Presence presence) {
        presence.setEntryTime(LocalTime.of(0, 0));
        presence.setExitTime(LocalTime.of(0, 0));
    }

    // Metodo per evitare i null nei tempi di ingresso e uscita
    private LocalTime defaultTime(LocalTime time) {
        return time != null ? time : LocalTime.of(0, 0);
    }


    private double calculateWorkedHours(LocalTime entryTime, LocalTime exitTime) {
        if (entryTime == null || exitTime == null || entryTime.isAfter(exitTime)) {
            return 0.0;
        }

        Duration duration = Duration.between(entryTime, exitTime);
        return duration.toHours() > 4 ? duration.toHours() - 1 : duration.toHours();
    }

    private void validatePresenceDates(List<PresenceDto> presenceDtoList, Integer yearRequest, Integer monthRequest) throws InconsistencyDatetimeException {
        for (PresenceDto presence : presenceDtoList) {
            LocalDate workDay = presence.getWorkDay();
            if (workDay == null || !(workDay.getYear() == yearRequest) || !(workDay.getMonthValue() == monthRequest)) {
                throw new InconsistencyDatetimeException("Anno o mese incongruente con un giorno lavorativo");
            }
        }
    }
}
