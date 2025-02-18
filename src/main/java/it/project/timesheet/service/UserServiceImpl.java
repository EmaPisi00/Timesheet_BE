package it.project.timesheet.service;

import it.project.timesheet.entity.User;
import it.project.timesheet.repository.UserRepository;
import it.project.timesheet.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return null;
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
}
