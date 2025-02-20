package it.project.timesheet.service.common;

import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    User save(User user) throws BaseException;

    User findByUuid(UUID uuid);

    User updateByUuid(User user, UUID uuid);

    void deleteByUuid(UUID uuid);

    List<User> findAll();
}
