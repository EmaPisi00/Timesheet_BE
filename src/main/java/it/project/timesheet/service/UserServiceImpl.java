package it.project.timesheet.service;

import io.micrometer.common.util.StringUtils;
import it.project.timesheet.domain.dto.UserDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.BadRequestException;
import it.project.timesheet.exception.UnauthorizedException;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.exception.custom.ObjectFoundException;
import it.project.timesheet.exception.custom.ObjectNotFoundException;
import it.project.timesheet.repository.UserRepository;
import it.project.timesheet.service.base.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User save(UserDto userDto) throws BaseException {
        if (userDto.getUuid() != null && StringUtils.isNotBlank(userDto.getUuid().toString())) {
            throw new BadRequestException("UUID presente :  " + userDto.getUuid().toString());
        }

        if (StringUtils.isBlank(userDto.getPassword()) || StringUtils.isBlank(userDto.getEmail())) {
            throw new BadRequestException("Email o Password non inserite");
        }

        if (existsUserByEmail(userDto.getEmail())) {
            throw new ObjectFoundException("Email già esistente");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());


        return persistOnMysql(user);
    }

    @Override
    public User findByUuid(UUID uuid) throws BaseException {
        User user = userRepository.findByUuidAndDeletedAtIsNull(uuid).
                orElseThrow(() -> new ObjectNotFoundException("User non trovato non questo UUID: " + uuid.toString()));
        log.info("User trovato {}", user);
        return user;
    }

    @Override
    public User updateByUuid(UserDto userDto, UUID uuid) throws BaseException {
        User userFound = findByUuid(uuid);

        if (StringUtils.isBlank(userDto.getPassword()) || StringUtils.isBlank(userDto.getEmail())) {
            throw new BadRequestException("Email o Password non inserite");
        }

        userFound.setPassword(userDto.getPassword());
        userFound.setEmail(userDto.getEmail());

        return persistOnMysql(userFound);
    }

    @Override
    public void deleteByUuid(UUID uuid) throws BaseException {
        User user = findByUuid(uuid);
        user.deleted();
        log.info("User eliminato (logicamente) {}", user);

        // prevedere un metodo di Facade che quando elimino logicamente un utente questo deve
        // cancellare logicamente a cascata in tutte le tabelle in cui viene richiamato.
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByEmail(String email) throws BaseException {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new ObjectNotFoundException("User non trovato con questa email: " + email));
        log.info("User trovato {}", user);
        return user;
    }

    private boolean existsUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email).isPresent(); // Restituisce true se trovato, false se non trovato
    }

    private User persistOnMysql(User user) {
        user = userRepository.save(user);
        log.info("User salvato {}", user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User non trovato con email: " + email));
    }
}
