package it.project.timesheet.service;

import it.project.timesheet.domain.dto.PresenceDto;
import it.project.timesheet.domain.dto.request.TimesheetRequestDto;
import it.project.timesheet.domain.dto.TimesheetDto;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.domain.enums.StatusDayEnum;
import it.project.timesheet.domain.enums.StatusHoursEnum;
import it.project.timesheet.exception.BadRequestException;
import it.project.timesheet.repository.TimesheetRepository;
import it.project.timesheet.service.facade.TimesheetFacadeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TimesheetServiceTest {

    @Mock
    private TimesheetRepository timesheetRepository;

    @InjectMocks
    private TimesheetServiceImpl timesheetService;

    @Mock
    private EmployeeServiceImpl employeeService;

    @Mock
    private PresenceServiceImpl presenceService;

    @InjectMocks
    private TimesheetFacadeImpl timesheetFacadeImpl;

    private TimesheetRequestDto timesheetRequestDto;

    @Test
    void getTimesheet() {
        // Creo nuovo user
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("email@email.com");
        user.setPassword("password");

        // Creo nuovo Dipendente(Employee)
        Employee employee = new Employee();
        employee.setUuid(UUID.randomUUID());
        employee.setName("Mario");
        employee.setSurname("Rossi");
        employee.setUser(user);

        // Creo nuovo timesheet
        Timesheet timesheet = new Timesheet();
        timesheet.setUuid(UUID.randomUUID());
        timesheet.setYear(2024);
        timesheet.setMonth(12);
        timesheet.setEmployee(employee);

        given(timesheetRepository.findAllByDeletedAtIsNull())
                .willReturn(List.of(timesheet));
        var personList = timesheetService.findAll();
        assertThat(personList).isNotNull();
    }

    @BeforeEach
    void setUp() {
        // Creo uno user
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("email@email.com");
        user.setPassword("password");

        // Crea un dipendente fittizio
        Employee employee = new Employee();
        employee.setUuid(UUID.randomUUID());
        employee.setName("Mario");
        employee.setSurname("Rossi");
        employee.setUser(user);

        // Crea il DTO del timesheet
        TimesheetDto timesheetDto = new TimesheetDto();
        timesheetDto.setYear(2024);
        timesheetDto.setMonth(3);
        timesheetDto.setName(employee.getName());
        timesheetDto.setSurname(employee.getSurname());
        timesheetDto.setUser(user);

        // Crea una presenza fittizia
        PresenceDto presenceDto = new PresenceDto();
        presenceDto.setWorkDay(LocalDate.of(2024, 3, 15));
        presenceDto.setEntryTime(LocalTime.of(9, 0));
        presenceDto.setExitTime(LocalTime.of(18, 0));
        presenceDto.setStatusDayEnum(StatusDayEnum.WORKDAY);
        presenceDto.setStatusHoursEnum(StatusHoursEnum.NORMAL_WORKING);
        presenceDto.setIllnessed(false);
        presenceDto.setSmartWorking(false);

        // Crea il DTO della richiesta con la lista delle presenze
        timesheetRequestDto = new TimesheetRequestDto();
        timesheetRequestDto.setTimesheetDto(timesheetDto);
        timesheetRequestDto.setPresenceList(List.of(presenceDto));
    }

    @Test
    void checkRequestTimesheetTest() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> timesheetFacadeImpl.saveTimesheet(null));
        assertEquals("RequestTimesheetDto o la lista delle presenze è null", exception.getMessage());
    }



}
