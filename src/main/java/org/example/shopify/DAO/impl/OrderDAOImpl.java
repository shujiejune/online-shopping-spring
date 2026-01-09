package org.example.shopify.DAO.impl;

import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.Domain.Order;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDAOImpl implements OrderDAO {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Order> getOrderById(Long id) {
        return Optional.ofNullable(em.find(Order.class, id));
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return em.createQuery("FROM Order o WHERE o.user.id = :userId", Order.class)
                .setParameter("userId", userId).getResultList();
    }

    @Override
    public List<Order> getAllOrders() {
        return em.createQuery("FROM Order o", Order.class).getResultList();
    }

    @Override
    public long getTotalOrdersCount() {
        return em.createQuery("SELECT COUNT(o) FROM Order o", Long.class)
                .getSingleResult();
    }

    public List<Order> getPaginatedOrders(int page, int pageSize) {
        return em.createQuery(
                        "SELECT o FROM Order o JOIN FETCH o.user ORDER BY o.datePlaced DESC", Order.class)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public void saveOrder(Order order) {
        if  (order.getId() == null) {
            em.persist(order);
        } else {
            em.merge(order);
        }
    }
}
