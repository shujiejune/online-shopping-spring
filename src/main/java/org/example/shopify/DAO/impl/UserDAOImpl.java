package org.example.shopify.DAO.impl;

import org.example.shopify.DAO.UserDAO;
import org.example.shopify.Domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {
    private final SessionFactory sessionFactory;

    public UserDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    };

    @Override
    public Optional<User> getUserById(Long userId) {
        List<User> users = getSession().createQuery("FROM User u WHERE u.id = :userId", User.class)
                .setParameter("userId", userId).getResultList();
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        List<User> users = getSession().createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username).getResultList();
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        List<User> users = getSession().createQuery("FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email).getResultList();
        return users.stream().findFirst();
    }

    @Override
    public void saveUser(User user) {
        getSession().persist(user);
    }
}
