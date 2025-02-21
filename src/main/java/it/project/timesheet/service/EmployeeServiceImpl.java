package it.project.timesheet.service;

import io.micrometer.common.util.StringUtils;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.BadRequestException;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.exception.custom.ObjectNotFoundException;
import it.project.timesheet.repository.EmployeeRepository;
import it.project.timesheet.service.base.EmployeeService;
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
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    @Override
    public Employee save(Employee employee) throws BaseException {
        if (employee.getUuid() != null && StringUtils.isNotBlank(employee.getUuid().toString())) {
            throw new BadRequestException("UUID presente :  " + employee.getUuid().toString());
        }

        User user = userService.findByUuid(employee.getUser().getUuid());

        if (StringUtils.isBlank(employee.getName())
                || StringUtils.isBlank(employee.getSurname())) {
            throw new BadRequestException("Nome o Cognome non inseriti");
        }

        employee.setUser(user);

        return persistOnMysql(employee);
    }

    @Override
    public Employee findByUuid(UUID uuid) throws BaseException {
        Employee employee = employeeRepository.findByUuidAndDeletedAtIsNull(uuid).
                orElseThrow(() -> new ObjectNotFoundException("Employee non trovato con questo UUID: " + uuid.toString()));
        log.info("Employee trovato {}", employee);
        return employee;
    }

    @Override
    public Employee updateByUuid(Employee employee, UUID uuid) throws BaseException {
        Employee employeeFound = findByUuid(uuid);
        User userFound = userService.findByUuid(employee.getUser().getUuid());

        if (StringUtils.isBlank(employee.getName())
                || StringUtils.isBlank(employee.getSurname())) {
            throw new BadRequestException("Nome o Cognome non inseriti");
        }

        employeeFound.setName(employee.getName());
        employeeFound.setSurname(employee.getSurname());
        employeeFound.setUser(userFound);

        return persistOnMysql(employeeFound);
    }

    @Override
    public void deleteByUuid(UUID uuid) throws BaseException {
        Employee employee = findByUuid(uuid);
        employee.deleted();
        log.info("Employee eliminato (logicamente) {}", employee);

        // prevedere un metodo di Facade che quando elimino logicamente un utente questo deve
        // cancellare logicamente a cascata in tutte le tabelle in cui viene richiamato.
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAllByDeletedAtIsNull();
    }

    private Employee persistOnMysql(Employee employee) {
        employee = employeeRepository.save(employee);
        log.info("Employee salvato {}", employee);
        return employee;
    }
}
