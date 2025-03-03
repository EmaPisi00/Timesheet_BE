package it.project.timesheet.repository;

import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByUuidAndDeletedAtIsNull(UUID uuid);

    List<Employee> findAllByDeletedAtIsNull();

    Optional<Employee> findByUserAndDeletedAtIsNull(User user);
}
