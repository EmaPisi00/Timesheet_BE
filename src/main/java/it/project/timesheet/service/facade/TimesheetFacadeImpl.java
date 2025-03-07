package it.project.timesheet.service.facade;

import it.project.timesheet.domain.dto.PresenceDto;
import it.project.timesheet.domain.dto.RequestTimesheetDto;
import it.project.timesheet.domain.dto.TimesheetDto;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.domain.enums.TypeDayEnum;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.exception.custom.InconsistencyDatetimeException;
import it.project.timesheet.exception.custom.ObjectFoundException;
import it.project.timesheet.service.base.EmployeeService;
import it.project.timesheet.service.base.PresenceService;
import it.project.timesheet.service.base.TimesheetService;
import it.project.timesheet.service.base.UserService;
import it.project.timesheet.service.facade.base.TimesheetFacade;
import it.project.timesheet.utils.DateUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
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
                presenceDto.setTypeDayEnum(TypeDayEnum.HOLIDAY);
            } else {
                presenceDto.setTypeDayEnum(TypeDayEnum.WORKDAY);
                holidayEntryTime = LocalTime.of(9, 0);
                holidayExitTime = LocalTime.of(18, 0);
            }

            presenceDto.setWorkDay(date);
            presenceDto.setEntryTime(holidayEntryTime);
            presenceDto.setExitTime(holidayExitTime);
            presenceList.add(presenceDto);
        }

        requestTimesheetDto.setPresenceList(presenceList);

        return requestTimesheetDto;
    }

    @Override
    public List<Presence> saveTimesheet(RequestTimesheetDto requestTimesheetDto) throws BaseException {
        Set<LocalDate> holidays = DateUtils.getHolidays(requestTimesheetDto.getTimesheetDto().getYear());

        // Verifico che l'anno ed il mese passato corrispondono con i giorni lavorativi
        List<PresenceDto> presenceDtoList = requestTimesheetDto.getPresenceList();
        Integer yearRequest = requestTimesheetDto.getTimesheetDto().getYear();
        Integer monthRequest = requestTimesheetDto.getTimesheetDto().getMonth();

        for (PresenceDto ithPresenceDto : presenceDtoList) {
            LocalDate workDay = ithPresenceDto.getWorkDay();
            Integer ithYear = workDay.getYear();
            Integer ithMonth = workDay.getMonthValue();

            if (!ithYear.equals(yearRequest) || !ithMonth.equals(monthRequest)) {
                throw new InconsistencyDatetimeException("Anno o mese incongruente con un giorno lavorativo");
            }
        }

        // Recupero l'employee (Dipendente)
        Employee employee = employeeService.findByUser(requestTimesheetDto.getTimesheetDto().getUser().getUuid());

        Timesheet timesheet = Timesheet.builder()
                .year(yearRequest)
                .month(monthRequest)
                .employee(employee)
                .build();

        // Salvo il nuovo timesheet a DB
        timesheetService.save(timesheet);

        List<Presence> presenceList = new ArrayList<>();
        for (PresenceDto ithPresenceDto : requestTimesheetDto.getPresenceList()) {
            Presence presence = new Presence();
            LocalDate day = ithPresenceDto.getWorkDay();
            DayOfWeek dayOfWeek = day.getDayOfWeek();

            // Calcolo e controllo se il giorno corrente è un giorno festivo o un weekend
            boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
            boolean isHoliday = holidays.contains(day);

            LocalTime entryTime = ithPresenceDto.getEntryTime();
            LocalTime exitTime = ithPresenceDto.getExitTime();

            // Calcolo l'orario complessivo di lavoro e sottraggo 1 ora (per la pausa pranzo) se ha lavorato almeno 5 ore
            Duration durationWorkDay = Duration.between(entryTime, exitTime);
            if (durationWorkDay.toHours() > 4) {
                durationWorkDay = durationWorkDay.minus(Duration.ofHours(1));
            }

            if ((entryTime.isAfter(exitTime) || entryTime.equals(exitTime))) {
                throw new InconsistencyDatetimeException("Orario entrata superiore all'orario di uscita");
            }

            if ((durationWorkDay.toHours() < 4 && (!isHoliday && !isWeekend))) {
                entryTime = LocalTime.of(0, 0);
                exitTime = LocalTime.of(0, 0);
                isHoliday = Boolean.TRUE;
            }

            if ((isWeekend || isHoliday) && ((durationWorkDay.toHours() > 4))) {
                presence.setDescription(TypeDayEnum.WORKDAY.toString());
            } else {
                presence.setDescription(TypeDayEnum.HOLIDAY.toString());
                entryTime = LocalTime.of(0, 0);
                exitTime = LocalTime.of(0, 0);
            }

            presence.setWorkDay(day);
            presence.setEntryTime(entryTime);
            presence.setExitTime(exitTime);
            presence.setTimesheet(timesheet);
            presenceList.add(presence);
        }

        return presenceService.saveAll(presenceList);
    }
}
