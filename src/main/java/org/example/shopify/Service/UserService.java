package org.example.shopify.Service;

import org.example.shopify.DAO.UserDAO;
import org.example.shopify.Domain.User;
import org.example.shopify.Exception.InvalidCredentialsException;
import org.example.shopify.Exception.UserAlreadyExistsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {
    private final UserDAO userDAO;
    private final BCryptPasswordEncoder pwdEncoder;

    public UserService(UserDAO userDAO, BCryptPasswordEncoder pwdEncoder) {
        this.userDAO = userDAO;
        this.pwdEncoder = pwdEncoder;
    }

    @Transactional
    public void register(User user) {
        if (userDAO.getUserByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userDAO.getUserByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        String encodedPwd = pwdEncoder.encode(user.getPassword());
        user.setPassword(encodedPwd);

        if (user.getRole() == null) {
            user.setRole(1);
        }

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
