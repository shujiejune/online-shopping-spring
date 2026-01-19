package org.example.shopify.DAO;

import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.Product;

import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    Optional<Order> getOrderById(Long orderId);
    List<Order> getOrdersByUserId(Long userId);
    List<Order> getPaginatedOrdersByUserId(Long userId, int page, int pageSize);
    List<Order> getAllOrders();
    long getOrdersCountByUserId(Long userId);
    long getTotalOrdersCount();
    List<Order> getPaginatedOrders(int page, int pageSize);
    void saveOrder(Order order);
    List<Product> getMostPopularProducts(int limit);
    List<Product> getMostProfitableProducts(int limit);
}
