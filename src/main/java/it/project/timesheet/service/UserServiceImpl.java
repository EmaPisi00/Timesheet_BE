package it.project.timesheet.service;

import io.micrometer.common.util.StringUtils;
import it.project.timesheet.entity.User;
import it.project.timesheet.repository.UserRepository;
import it.project.timesheet.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        if(user.getUuid() != null && StringUtils.isNotBlank(user.getUuid().toString())){
            // gestione errore: caso di uuid inserito in input
            return null;
        }

        if(StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getEmail())){
            // gestione errore: caso di Password/Email
            return null;
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

    private User persistOnMysql(User user){
        return userRepository.save(user);
    }
}
