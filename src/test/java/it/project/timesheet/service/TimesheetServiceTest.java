package it.project.timesheet.service;

import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.Timesheet;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.repository.TimesheetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TimesheetServiceTest {

    @Mock
    private TimesheetRepository timesheetRepository;

    @InjectMocks
    private TimesheetServiceImpl timesheetService;

    // Example Mock
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
        //Then
        //Make sure to import assertThat From org.assertj.core.api package
        assertThat(personList).isNotNull();
    }
}
