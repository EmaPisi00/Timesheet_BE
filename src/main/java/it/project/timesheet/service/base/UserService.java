package it.project.timesheet.service.base;

import it.project.timesheet.domain.dto.UserDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.UnauthorizedException;
import it.project.timesheet.exception.common.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    User save(UserDto userDto) throws BaseException;

    User findByUuid(UUID uuid) throws BaseException;

    User updateByUuid(UserDto userDto, UUID uuid) throws BaseException;

    void deleteByUuid(UUID uuid) throws BaseException;

    List<User> findAll();

    User findByEmail(String email) throws BaseException;

    User login(String email, String password) throws BaseException;
}
