package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User buildUser(boolean active) {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("hashedPassword123");
        user.setActive(active);
        return user;
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExistsAndActive() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(buildUser(true)));

        UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe");

        assertEquals("john.doe", userDetails.getUsername());
        assertEquals("hashedPassword123", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_shouldReturnDisabledUserDetails_whenUserInactive() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(buildUser(false)));

        UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe");

        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"));
    }
}
