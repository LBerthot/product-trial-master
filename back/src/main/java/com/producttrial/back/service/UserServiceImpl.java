package com.producttrial.back.service;

import com.producttrial.back.entity.User;
import com.producttrial.back.exception.UserNotFoundException;
import com.producttrial.back.repository.UserRepository;
import com.producttrial.back.service.iservice.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        log.debug("Fetching user with id {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Fetching user with email {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User saved = userRepository.save(user);
            log.info("Saved user with id {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Error saving user", e);
            throw e;
        }
    }

    //A adpater => peut etre faire un update sans le mdp et un avec que le mdp pour changer mdp
    @Override
    public User update(Long id, User user) {
        return userRepository.findById(id).map(u -> {
            u.setUsername(user.getUsername());
            u.setEmail(user.getEmail());
            u.setFirstname(user.getFirstname());
            u.setPassword(user.getPassword());
            User saved = userRepository.save(u);
            log.info("Updated user with id {}", saved.getId());
            return saved;
        }).orElseThrow(() -> {
            log.warn("Product not found for update id={}", id);
            return new UserNotFoundException(id);
        });
    }

    @Override
    public void delete(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                log.warn("Attempted to delete non-existing user id={}", id);
                throw new UserNotFoundException(id);
            }
            userRepository.deleteById(id);
            log.info("Deleted user id={}", id);
        } catch (Exception ex) {
            log.error("Error deleting user id={}", id, ex);
            throw ex;
        }
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
        log.info("Deleted all users");
    }
}
