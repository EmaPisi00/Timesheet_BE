package it.project.timesheet.service;

import io.micrometer.common.util.StringUtils;
import it.project.timesheet.configuration.JwtTokenConfiguration;
import it.project.timesheet.domain.dto.request.UserRequestDto;
import it.project.timesheet.domain.dto.response.AuthResponseDto;
import it.project.timesheet.domain.entity.User;
import it.project.timesheet.exception.BadRequestException;
import it.project.timesheet.exception.common.BaseException;
import it.project.timesheet.exception.custom.ObjectFoundException;
import it.project.timesheet.exception.custom.ObjectNotFoundException;
import it.project.timesheet.repository.UserRepository;
import it.project.timesheet.service.base.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
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

        if (existsUserByEmail(user.getEmail())) {
            throw new ObjectFoundException("Email già esistente");
        }

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
    public User updateByUuid(User user, UUID uuid) throws BaseException {
        User userFound = findByUuid(uuid);

        if (StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getEmail())) {
            throw new BadRequestException("Email o Password non inserite");
        }

        userFound.setPassword(user.getPassword());
        userFound.setEmail(user.getEmail());

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
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email);
    }

    private boolean existsUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email).isPresent(); // Restituisce true se trovato, false se non trovato
    }

    private User persistOnMysql(User user) {
        user = userRepository.save(user);
        log.info("User salvato {}", user);
        return user;
    }
}
