package it.project.timesheet.service.auth;

import it.project.timesheet.domain.entity.User;
import it.project.timesheet.repository.UserRepository;
import it.project.timesheet.service.base.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.findByEmail(username);
        if (user.isPresent()) {
            User userFound = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userFound.getEmail())
                    .password(userFound.getPassword())
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
