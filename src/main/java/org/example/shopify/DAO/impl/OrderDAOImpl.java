package org.example.shopify.DAO.impl;

import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.OrderStatus;
import org.example.shopify.Domain.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderDAOImpl implements OrderDAO {
    private final SessionFactory sessionFactory;

    public OrderDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return Optional.ofNullable(getSession().find(Order.class, id));
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return getSession().createQuery("SELECT DISTINCT o FROM Order o " +
                        "LEFT JOIN FETCH o.orderItems i " +
                        "LEFT JOIN FETCH i.product " +
                        "WHERE o.user.id = :userId", Order.class)
                .setParameter("userId", userId).getResultList();
    }

    @Override
    public List<Order> getPaginatedOrdersByUserId(Long userId, int page, int pageSize) {
        return getSession().createQuery(
                "FROM Order o WHERE o.user.id = :userId ORDER BY o.datePlaced DESC", Order.class)
                .setParameter("userId", userId)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Order> getAllOrders() {
        return getSession().createQuery("FROM Order o", Order.class).getResultList();
    }

    @Override
    public long getOrdersCountByUserId(Long userId) {
        return getSession().createQuery("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId", Long.class)
                .setParameter("userId", userId).getSingleResult();
    }

    @Override
    public long getTotalOrdersCount() {
        return getSession().createQuery("SELECT COUNT(o) FROM Order o", Long.class)
                .getSingleResult();
    }

    @Override
    public List<Order> getPaginatedOrders(int page, int pageSize) {
        return getSession().createQuery(
                        "SELECT o FROM Order o JOIN FETCH o.user ORDER BY o.datePlaced DESC", Order.class)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public void saveOrder(Order order) {
        if  (order.getId() == null) {
            getSession().persist(order);
        } else {
            getSession().merge(order);
        }
    }

    @Override
    public List<Product> getMostPopularProducts(int limit) {
        String hql = "SELECT oi.product " +
                "FROM Order o " +
                "JOIN o.orderItems oi " +
                "WHERE o.orderStatus = :status " +
                "GROUP BY oi.product " +
                "ORDER BY SUM(oi.quantity) DESC";

        return getSession().createQuery(hql, Product.class)
                .setParameter("status", OrderStatus.Completed)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<Product> getMostProfitableProducts(int limit) {
        String hql = "SELECT oi.product " +
                "FROM Order o " +
                "JOIN o.orderItems oi " +
                "WHERE o.orderStatus = :status " +
                "GROUP BY oi.product " +
                "ORDER BY SUM(oi.quantity * oi.purchasedPrice) DESC";

        return getSession().createQuery(hql, Product.class)
                .setParameter("status", OrderStatus.Completed)
                .setMaxResults(limit)
                .getResultList();
    }
}
