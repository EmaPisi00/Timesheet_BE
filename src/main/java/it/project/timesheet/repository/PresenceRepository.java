package it.project.timesheet.repository;

import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.domain.entity.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, UUID> {

    Optional<Presence> findByUuidAndDeletedAtIsNull(UUID uuid);

    List<Presence> findAllByDeletedAtIsNull();

    List<Presence> findAllByTimesheetAndDeletedAtIsNull(Timesheet timesheet);
}
