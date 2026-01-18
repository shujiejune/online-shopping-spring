package org.example.shopify.Service;

import org.example.shopify.DAO.UserDAO;
import org.example.shopify.DTO.RegistrationRequestDTO;
import org.example.shopify.Domain.User;
import org.example.shopify.Exception.InvalidCredentialsException;
import org.example.shopify.Exception.ResourceNotFoundException;
import org.example.shopify.Exception.UserAlreadyExistsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserDAO userDAO;
    private final BCryptPasswordEncoder pwdEncoder;

    public UserService(UserDAO userDAO, BCryptPasswordEncoder pwdEncoder) {
        this.userDAO = userDAO;
        this.pwdEncoder = pwdEncoder;
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Transactional
    public void register(RegistrationRequestDTO dto) {
        // 1. Check if username or email already exists
        if (userDAO.getUserByUsername(dto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userDAO.getUserByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // 2. Map DTO to Entity
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(pwdEncoder.encode(dto.getPassword()));
        user.setRole(1); // Default to Normal User

        // 3. Save
        userDAO.saveUser(user);
    }

    @Transactional
    public User login(String username, String password) {
        Optional<User> userOptional = userDAO.getUserByUsername(username);
        if (!userOptional.isPresent()) {
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }

        User user = userOptional.get();
        if (!pwdEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }

        return user;
    }
}
