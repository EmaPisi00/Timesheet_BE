package it.project.timesheet.service.facade;

import it.project.timesheet.domain.dto.PresenceDto;
import it.project.timesheet.domain.dto.request.TimesheetRequestDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = BaseException.class)
public class TimesheetFacadeImpl implements TimesheetFacade {

    private final TimesheetService timesheetService;
    private final EmployeeService employeeService;
    private final PresenceService presenceService;

    @Override
    public TimesheetRequestDto generateTimesheet(Integer month, Integer year, UUID uuidEmployee) throws BaseException {
        Set<LocalDate> holidays = DateUtils.getHolidays(year);
        TimesheetRequestDto timesheetRequestDto = new TimesheetRequestDto();
        Employee employee = employeeService.findByUuid(uuidEmployee);

        // Ricerca per mese e anno per vedere se esiste già un timesheet con quel mese ed anno
        if (timesheetService.existsTimesheetForMonthAndYearAndEmployeeAndLockedIsTrue(month, year, uuidEmployee)) {
            throw new ObjectFoundException("Timesheet già esistente e blocato già");
        } else if (timesheetService.existsTimesheetForMonthAndYearAndEmployeeAndLockedIsFalse(month, year, employee)) {
            Timesheet timesheet = timesheetService.findByMonthAndYearAndEmployee(month, year, uuidEmployee);

            // Setto il timesheet di output
            timesheetRequestDto.setTimesheetDto(createTimesheetDto(year, month, employee));

            // Setto le ore
            timesheetRequestDto.setPresenceList(convertToPresenceDtoList(timesheet.getPresenceList()));

        } else {
            timesheetRequestDto.setTimesheetDto(createTimesheetDto(year, month, employee));

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

            List<PresenceDto> sortedList = presenceList.stream()
                    .sorted(Comparator.comparing(PresenceDto::getWorkDay))
                    .collect(Collectors.toList());
            timesheetRequestDto.setPresenceList(sortedList);


        }
        return timesheetRequestDto;
    }

    @Override
    public List<Presence> saveTimesheet(TimesheetRequestDto timesheetRequestDto) throws BaseException {
        if (timesheetRequestDto == null || timesheetRequestDto.getPresenceList() == null || timesheetRequestDto.getTimesheetDto() == null) {
            throw new BadRequestException("RequestTimesheetDto o la lista delle presenze è null");
        }

        // Eseguo la ricerca per Employee
        Employee employee = employeeService.findByUser(timesheetRequestDto.getTimesheetDto().getUuidUser());

        List<Presence> presenceList = new ArrayList<>();

        System.out.println(timesheetRequestDto.getTimesheetDto());

        // Ricerca per mese e anno per vedere se esiste già un timesheet con quel mese ed anno
        if (timesheetService.existsTimesheetForMonthAndYearAndEmployeeAndLockedIsFalse(
                timesheetRequestDto.getTimesheetDto().getMonth(),
                timesheetRequestDto.getTimesheetDto().getYear(),
                employee)) {
            // richiamo la funzione di update
            presenceList = update(timesheetRequestDto, employee);
        } else {
            // richiamo la funzione di save
            presenceList = presenceService.saveAll(save(timesheetRequestDto, employee));
        }

        return presenceList;
    }

    private List<Presence> save(TimesheetRequestDto timesheetRequestDto, Employee employee) throws BaseException {

        List<Presence> presenceList = new ArrayList<>();

        // Ricavo mese, anno e i giorni di vacanze
        Integer yearRequest = timesheetRequestDto.getTimesheetDto().getYear();
        Integer monthRequest = timesheetRequestDto.getTimesheetDto().getMonth();
        Set<LocalDate> holidays = DateUtils.getHolidays(yearRequest);

        validatePresenceDates(timesheetRequestDto.getPresenceList(), yearRequest, monthRequest);

        Timesheet timesheet = Timesheet.builder()
                .year(yearRequest)
                .month(monthRequest)
                .employee(employee)
                .build();
        timesheetService.save(timesheet);


        for (PresenceDto ithPresenceDto : timesheetRequestDto.getPresenceList()) {
            LocalDate day = ithPresenceDto.getWorkDay();
            boolean isWeekend = (day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY);
            boolean isHoliday = holidays.contains(day);

            if (ithPresenceDto.isIllnessed() && ithPresenceDto.isSmartWorking()) {
                throw new BadRequestException("Errore sono settati a TRUE sia lo stato MALATTIA che SMART_WORKING");
            }

            Presence presence = createPresence(ithPresenceDto, day, isHoliday, isWeekend, timesheet);
            presenceList.add(presence);
        }

        return presenceList;
    }

    private List<Presence> update(TimesheetRequestDto timesheetRequestDto, Employee employee) throws BaseException {
        // Recupera il timesheet esistente dal database
        Timesheet existingTimesheet = timesheetService.findByMonthAndYearAndEmployee(
                timesheetRequestDto.getTimesheetDto().getMonth(),
                timesheetRequestDto.getTimesheetDto().getYear(),
                employee.getUuid()
        );

        List<Presence> updatedPresenceList = new ArrayList<>();

        // Creiamo una mappa per accedere rapidamente alle presenze esistenti tramite la data di lavoro
        Map<LocalDate, Presence> existingPresencesMap = new HashMap<>();
        for (Presence presence : presenceService.findByTimesheet(existingTimesheet)) {
            existingPresencesMap.put(presence.getWorkDay(), presence);
        }

        // Validiamo che le date delle nuove presenze siano coerenti con il mese e l'anno richiesti
        validatePresenceDates(timesheetRequestDto.getPresenceList(),
                timesheetRequestDto.getTimesheetDto().getYear(),
                timesheetRequestDto.getTimesheetDto().getMonth());

        for (PresenceDto ithPresenceDto : timesheetRequestDto.getPresenceList()) {
            LocalDate workDay = ithPresenceDto.getWorkDay();
            Presence presence;

            if (existingPresencesMap.containsKey(workDay)) {
                // Se la presenza esiste già, aggiorniamo i dati
                presence = existingPresencesMap.get(workDay);
                presence.setEntryTime(defaultTime(ithPresenceDto.getEntryTime()));
                presence.setExitTime(defaultTime(ithPresenceDto.getExitTime()));
                presence.setDescription(ithPresenceDto.getDescription());
            } else {
                // Se non esiste, creiamo una nuova presenza
                boolean isWeekend = (workDay.getDayOfWeek() == DayOfWeek.SATURDAY || workDay.getDayOfWeek() == DayOfWeek.SUNDAY);
                boolean isHoliday = DateUtils.getHolidays(timesheetRequestDto.getTimesheetDto().getYear()).contains(workDay);
                presence = createPresence(ithPresenceDto, workDay, isHoliday, isWeekend, existingTimesheet);
            }

            // Ricalcoliamo le ore lavorate e aggiorniamo lo stato della giornata
            double totalHours = calculateWorkedHours(presence.getEntryTime(), presence.getExitTime());
            presence.setTotalHours(totalHours);

            // Determiniamo lo stato delle ore lavorate in base al totale calcolato
            if (totalHours > 8) {
                presence.setStatusHoursEnum(StatusHoursEnum.EXTRAORDINARY.toString());
            } else if (totalHours >= 4 && totalHours < 8) {
                presence.setStatusHoursEnum(StatusHoursEnum.PERMISSION.toString());
            } else if (totalHours == 8 || totalHours == 0) {
                presence.setStatusHoursEnum(StatusHoursEnum.NORMAL_WORKING.toString());
            }

            updatedPresenceList.add(presenceService.updateByUuid(presence, presence.getUuid()));
        }

        return updatedPresenceList;
    }


    private TimesheetDto createTimesheetDto(Integer year, Integer month, Employee employee) {
        return TimesheetDto.builder()
                .year(year)
                .month(month)
                .uuidUser(employee.getUser().getUuid())
                .name(employee.getName())
                .surname(employee.getSurname())
                .build();
    }

    private List<PresenceDto> convertToPresenceDtoList(List<Presence> presenceSet) {
        return presenceSet.stream().map(presence -> {
            PresenceDto presenceDto = new PresenceDto();
            presenceDto.setWorkDay(presence.getWorkDay());
            presenceDto.setDescription(presence.getDescription());
            presenceDto.setEntryTime(presence.getEntryTime());
            presenceDto.setExitTime(presence.getExitTime());
            presenceDto.setStatusDayEnum(StatusDayEnum.valueOf(presence.getStatusDayEnum()));
            presenceDto.setStatusHoursEnum(StatusHoursEnum.valueOf(presence.getStatusHoursEnum()));
            return presenceDto;
        }).toList();
    }

    private Presence createPresence(PresenceDto dto, LocalDate day, boolean isHoliday, boolean isWeekend, Timesheet timesheet) {
        Presence presence = new Presence();
        presence.setWorkDay(day);
        presence.setEntryTime(defaultTime(dto.getEntryTime()));
        presence.setExitTime(defaultTime(dto.getExitTime()));
        presence.setDescription(dto.getDescription());

        // Determina lo status del giorno
        if (dto.isIllnessed()) {
            setZeroTime(presence);
            presence.setStatusDayEnum(StatusDayEnum.ILLNESS.toString());
        } else if (dto.isSmartWorking()) {
            presence.setStatusDayEnum(StatusDayEnum.SMART_WORKING.toString());
        } else if (dto.isHoliday()) {
            setZeroTime(presence);
            presence.setStatusDayEnum(StatusDayEnum.HOLIDAY.toString());
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
    private void setZeroTime(it.project.timesheet.domain.entity.Presence presence) {
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
