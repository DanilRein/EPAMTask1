package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    public void authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Authentication failed: username or password is invalid");
                    return new IllegalArgumentException("Username or password is invalid");
                });
        if (!user.getPassword().equals(password)) {
            logger.warn("Authentication failed: username or password is invalid");
            throw new IllegalArgumentException("Username or password is invalid");
        }
    }
}
