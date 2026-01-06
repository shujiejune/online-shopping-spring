package org.example.shopify.DAO;

import org.example.shopify.Domain.User;

import java.util.Optional;

public interface UserDAO {
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    void saveUser(User user);
}
