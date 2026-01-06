package org.example.shopify.Service;

import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.Domain.*;
import org.example.shopify.Exception.NotEnoughInventoryException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    public OrderService(OrderDAO orderDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderDAO.getOrdersByUserId(userId);
    }

    public List<Product> getRecentlyPurchasedProducts(Long userId, int limit) {
        return productDAO.getProductsByUserId(userId, limit);
    }

    @Transactional
    public void createOrder(User user, Map<Long, Integer> items) {
        Order order = new Order();
        order.setUser(user);
        order.setDatePlaced(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.Processing);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            Product product = productDAO.getProductById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < quantity) {
                throw new NotEnoughInventoryException("Not enough inventory for " + product.getName());
            }

            product.setQuantity(product.getQuantity() - quantity);
            productDAO.saveOrUpdateProduct(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItem.setQuantity(quantity);
            orderItem.setPurchasedPrice(product.getRetailPrice());
            orderItem.setWholesalePrice(product.getWholesalePrice());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        orderDAO.saveOrder(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderDAO.getOrderById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.Cancelled) {
            return;
        }
        if (order.getOrderStatus() == OrderStatus.Completed) {
            throw new RuntimeException("Cannot cancel completed order.");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            int rolledbackQuantity = product.getQuantity() + orderItem.getQuantity();
            product.setQuantity(rolledbackQuantity);
            productDAO.saveOrUpdateProduct(product);
        }

        order.setOrderStatus(OrderStatus.Cancelled);
        orderDAO.saveOrder(order);
    }

    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderDAO.getOrderById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.Processing) {
            throw new RuntimeException("Cannot complete order that is not in progress.");
        }

        order.setOrderStatus(OrderStatus.Completed);
        orderDAO.saveOrder(order);
    }
}
