package org.example.shopify.DAO.impl;

import org.example.shopify.DAO.UserDAO;
import org.example.shopify.Domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> getUserByUsername(String username) {
        List<User> users = em.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username).getResultList();
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        List<User> users = em.createQuery("FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email).getResultList();
        return users.stream().findFirst();
    }

    @Override
    public void saveUser(User user) {
        em.persist(user);
    }
}
