package org.example.shopify.DAO;

import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.User;

import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    Optional<Order> getOrderById(Long orderId);
    List<Order> getOrdersByUserId(Long userId);
    List<Order> getAllOrders();
    void saveOrder(Order order);
}
