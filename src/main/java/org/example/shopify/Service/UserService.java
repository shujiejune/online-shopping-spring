package org.example.shopify.Service;

import org.example.shopify.DAO.UserDAO;
import org.example.shopify.Domain.User;
import org.example.shopify.Exception.InvalidCredentialsException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    public void register(User user) {
        if (userDAO.getUserByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userDAO.getUserByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        user.setRole(1);

        userDAO.saveUser(user);
    }

    public User login(String username, String password) {
        Optional<User> userOptional = userDAO.getUserByUsername(username);
        if (!userOptional.isPresent()) {
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }

        User user = userOptional.get();
        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }

        return user;
    }
}
