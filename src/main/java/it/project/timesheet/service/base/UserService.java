package it.project.timesheet.service.base;

import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    User save(User user) throws BaseException;

    User findByUuid(UUID uuid) throws BaseException;

    User updateByUuid(User user, UUID uuid) throws BaseException;

    void deleteByUuid(UUID uuid) throws BaseException;

    List<User> findAll();
}
