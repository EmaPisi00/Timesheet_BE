package it.project.timesheet.service;

import io.micrometer.common.util.StringUtils;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.BadRequestException;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.repository.UserRepository;
import it.project.timesheet.service.base.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) throws BaseException {
        if (user.getUuid() != null && StringUtils.isNotBlank(user.getUuid().toString())) {
            throw new BadRequestException("UUID presente :  " + user.getUuid().toString());
        }

        if (StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getEmail())) {
            throw new BadRequestException("Email o Password non inserite");
        }
        return persistOnMysql(user);
    }

    @Override
    public User findByUuid(UUID uuid) {
        return null;
    }

    @Override
    public User updateByUuid(User user, UUID uuid) {
        return null;
    }

    @Override
    public void deleteByUuid(UUID uuid) {

    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    private User persistOnMysql(User user) {
        return userRepository.save(user);
    }
}
