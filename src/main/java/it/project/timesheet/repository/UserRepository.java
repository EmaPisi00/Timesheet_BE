package it.project.timesheet.repository;

import it.project.timesheet.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUuidAndDeletedAtIsNull(UUID uuid);

    List<User> findAllByDeletedAtIsNull();

    Optional<User> findByEmailAndDeletedAtIsNull(String email);
}
