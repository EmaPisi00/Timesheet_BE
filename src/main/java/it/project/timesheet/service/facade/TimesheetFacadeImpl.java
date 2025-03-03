package it.project.timesheet.service.facade;

import it.project.timesheet.domain.dto.PresenceDto;
import it.project.timesheet.domain.dto.RequestTimesheetDto;
import it.project.timesheet.domain.dto.TimesheetDto;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.domain.enums.TypeDayEnum;
import it.project.timesheet.exception.common.BaseException;
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
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
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

        // Recupero l'employee
        Employee employee = employeeService.findByUser(requestTimesheetDto.getTimesheetDto().getUser().getUuid());

        Timesheet timesheet = Timesheet.builder()
                    .year(requestTimesheetDto.getTimesheetDto().getYear())
                    .month(requestTimesheetDto.getTimesheetDto().getMonth())
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

            LocalTime holidayEntryTime = LocalTime.of(0, 0);
            LocalTime holidayExitTime = LocalTime.of(0, 0);

            if (isWeekend || isHoliday) {
                presence.setDescription(TypeDayEnum.HOLIDAY.toString());
            } else {
                presence.setDescription(TypeDayEnum.WORKDAY.toString());
                holidayEntryTime = LocalTime.of(9, 0);
                holidayExitTime = LocalTime.of(18, 0);
            }

            presence.setWorkDay(day);
            presence.setEntryTime(holidayEntryTime);
            presence.setExitTime(holidayExitTime);
            presence.setTimesheet(timesheet);
            presenceList.add(presence);
        }

        return presenceService.saveAll(presenceList);
    }
}
