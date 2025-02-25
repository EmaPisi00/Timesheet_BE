package it.project.timesheet.repository;

import it.project.timesheet.domain.entity.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, UUID> {

    Optional<Timesheet> findByUuidAndDeletedAtIsNull(UUID uuid);

    List<Timesheet> findAllByDeletedAtIsNull();
}
