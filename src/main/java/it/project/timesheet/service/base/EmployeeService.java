package it.project.timesheet.service.base;

import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface EmployeeService {

    Employee save(Employee employee) throws BaseException;

    Employee findByUuid(UUID uuid) throws BaseException;

    Employee updateByUuid(Employee employee, UUID uuid) throws BaseException;

    void deleteByUuid(UUID uuid) throws BaseException;

    List<Employee> findAll();

    Employee findByUser(UUID uuidUser) throws BaseException;
}
