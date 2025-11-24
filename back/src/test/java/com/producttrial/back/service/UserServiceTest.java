package com.producttrial.back.service;

import com.producttrial.back.entity.User;
import com.producttrial.back.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAll_returnsListFromRepository() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> result = userService.findAll();
        assertEquals(2, result.size(), "list should contain 2 elements");
    }

    @Test
    void findById_whenPresent_returnsOptionalUser() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);
        assertTrue(result.isPresent(), "result should be present");
        assertEquals(1L, result.get().getId(), "id should be 1");
    }

    @Test
    void findById_whenNotPresent_returnsEmptyOptional() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(2L);
        assertTrue(result.isEmpty(), "result should be empty");
    }

    @Test
    void findByEmail_whenPresent_returnsOptionalUser() {
        User user = new User();
        user.setEmail("TEST");
        user.setId(1L);
        when(userRepository.findByEmail("TEST")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("TEST");
        assertTrue(result.isPresent(), "result should be present");
        assertEquals("TEST", result.get().getEmail(), "email should be TEST");
        assertEquals(1L, result.get().getId(), "id should be 1");
    }

    @Test
    void findByEmail_whenNotPresent_returnsEmptyOptional() {
        when(userRepository.findByEmail("TEST")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("TEST");
        assertTrue(result.isEmpty(), "result should be empty");
    }

    @Test
    void save_returnsSavedUser() {
        User user = User.builder()
                .email("test@test.fr")
                .username("test")
                .password("test!123")
                .build();

        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "hashed_" + invocation.getArgument(0));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        User saved = userService.save(user);

        assertNotNull(saved.getId(), "id should not be null");
        assertEquals(1L, saved.getId(), "id should be 1");
        assertEquals("test@test.fr", saved.getEmail(), "email should be test@test.fr");
        assertEquals("test", saved.getUsername(), "username should be test");
        assertEquals("hashed_test!123", saved.getPassword(), "password should be ");
    }

    @Test
    void updateUser_existingUser_updatesFields() {
        Long id = 1L;
        User user = User.builder()
                .id(id)
                .email("test@test.fr")
                .username("test")
                .password("test")
                .build();
        User changed = User.builder()
                .firstname("Test changed")
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.update(id, changed);

        assertEquals("Test changed", saved.getFirstname(), "firstname should be Test changed");
        assertEquals(user.getEmail(), saved.getEmail(), "email should be unchanged");
        assertEquals(user.getPassword(), saved.getPassword(), "password should be unchanged");
    }

    @Test
    void updateUser_nonExistingUser_throwsRuntimeException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        User user = User.builder()
                .firstname("Test changed")
                .build();

        assertThrows(RuntimeException.class, () -> userService.update(2L, user));
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        when(userRepository.existsById(5L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(5L);

        userService.delete(5L);

        verify(userRepository, times(1)).deleteById(5L);
    }

    @Test
    void deleteAll_callsRepositoryDeleteAll() {
        userService.deleteAll();

        verify(userRepository, times(1)).deleteAll();
    }

}
