package it.project.timesheet.controller;

import it.project.timesheet.controller.api.EmployeeApi;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.service.base.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmployeeController implements EmployeeApi {
    private final EmployeeService employeeService;

    @Override
    public Employee save(Employee employee) throws BaseException {
        return employeeService.save(employee);
    }

    @Override
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @Override
    public Employee findById(UUID uuid) throws BaseException {
        return employeeService.findByUuid(uuid);
    }

    @Override
    public Employee update(UUID uuid, Employee employee) throws BaseException {
        return employeeService.updateByUuid(employee, uuid);
    }

    @Override
    public void delete(UUID uuid) throws BaseException {
        employeeService.deleteByUuid(uuid);
    }
}
