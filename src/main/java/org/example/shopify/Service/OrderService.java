package org.example.shopify.Service;

import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DAO.UserDAO;
import org.example.shopify.DTO.OrderItemDTO;
import org.example.shopify.DTO.OrderPageResponseDTO;
import org.example.shopify.DTO.OrderRequestDTO;
import org.example.shopify.DTO.OrderResponseDTO;
import org.example.shopify.Domain.*;
import org.example.shopify.Exception.IllegalOrderStateException;
import org.example.shopify.Exception.NotEnoughInventoryException;
import org.example.shopify.Exception.ResourceNotFoundException;
import org.example.shopify.Mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final UserDAO userDAO;
    private final OrderMapper orderMapper;

    public OrderService(OrderDAO orderDAO, ProductDAO productDAO, UserDAO userDAO,  OrderMapper orderMapper) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        this.userDAO = userDAO;
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    public Order getOrderDetails(Long orderId) {
        return orderDAO.getOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Transactional(readOnly = true)
    public OrderPageResponseDTO getOrdersByUserId(Long userId, int page) {
        int pageSize = 5;
        // 1. Fetch the specific page of Order entities
        List<Order> orders = orderDAO.getPaginatedOrdersByUserId(userId, page, pageSize);

        // 2. Fetch total count for pagination math
        long totalElements = orderDAO.getOrdersCountByUserId(userId);

        // 3. Map Entities to DTOs
        List<OrderResponseDTO> orderDtos = orderMapper.mapToOrderResponseDTOList(orders);

        // 4. Wrap in Page Response
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return new OrderPageResponseDTO(orderDtos, page, totalPages, totalElements);
    }

    @Transactional
    public Order createOrder(Long userId, OrderRequestDTO request) {
        // 1. Fetch the User
        User user = userDAO.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Create the parent Order entity
        Order order = new Order();
        order.setUser(user);
        order.setDatePlaced(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.Processing);

        List<OrderItem> orderItems = new ArrayList<>();

        // 3. Process each item from the Request DTO
        for (OrderItemDTO itemDto : request.getItems()) {
            // Fetch the Product (validate existence)
            Product product = productDAO.getProductById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemDto.getProductId()));

            // Check Stock
            if (product.getQuantity() < itemDto.getQuantity()) {
                throw new NotEnoughInventoryException("Insufficient stock for: " + product.getName());
            }

            // Deduct Inventory
            product.setQuantity(product.getQuantity() - itemDto.getQuantity());
            productDAO.saveOrUpdateProduct(product);

            // Create OrderItem (Mapping DTO -> Entity)
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());

            // CRITICAL: Lock the current retail price as the execution price
            orderItem.setPurchasedPrice(product.getRetailPrice());

            orderItems.add(orderItem);
        }

        // 4. Link items to order and save
        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(order));
        orderDAO.saveOrder(order);

        return order;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderDAO.getOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.Cancelled) {
            throw new IllegalOrderStateException("This order is already cancelled.");
        }
        if (order.getOrderStatus() == OrderStatus.Completed) {
            throw new IllegalOrderStateException("Cannot cancel completed order.");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            int rolledbackQuantity = product.getQuantity() + orderItem.getQuantity();
            product.setQuantity(rolledbackQuantity);
            productDAO.saveOrUpdateProduct(product);
        }

        order.setOrderStatus(OrderStatus.Cancelled);
        orderDAO.saveOrder(order);

        return order;
    }

    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = orderDAO.getOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.Processing) {
            throw new IllegalOrderStateException("Cannot complete order that is not in progress.");
        }

        order.setOrderStatus(OrderStatus.Completed);
        orderDAO.saveOrder(order);

        return order;
    }

    private Double calculateTotalAmount(Order order) {
        return order.getOrderItems().stream()
                .mapToDouble(item -> item.getPurchasedPrice() * item.getQuantity())
                .sum();
    }
}
