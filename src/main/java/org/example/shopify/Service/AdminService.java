package org.example.shopify.Service;

import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.OrderItem;
import org.example.shopify.Domain.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final OrderDAO orderDAO;

    public AdminService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    public int getTotalSoldItems() {
        List<Order> orders = orderDAO.getAllOrders();
        int totalSum = 0;

        for (Order order : orders) {
            if (order.getOrderStatus() == OrderStatus.Completed) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    totalSum += orderItem.getQuantity();
                }
            }
        }

        return totalSum;
    }

    public double getTotalProfit() {
        List<Order> orders = orderDAO.getAllOrders();
        double totalProfit = 0;

        for (Order order : orders) {
            if (order.getOrderStatus() == OrderStatus.Completed) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    totalProfit += orderItem.getQuantity() * (orderItem.getPurchasedPrice() - orderItem.getWholesalePrice());
                }
            }
        }

        return totalProfit;
    }
}
