package it.project.timesheet.service.base;

import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface EmployeeService {

    Employee save(Employee employee) throws BaseException;

    Employee findByUuid(UUID uuid) throws BaseException;

    Employee updateByUuid(Employee employee, UUID uuid) throws BaseException;

    void deleteByUuid(UUID uuid) throws BaseException;

    Page<Employee> findAll(Pageable pageable);

    Employee findByUser(UUID uuidUser) throws BaseException;
}
