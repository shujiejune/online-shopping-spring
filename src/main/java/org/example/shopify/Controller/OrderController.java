package org.example.shopify.Controller;

import org.example.shopify.DAO.UserDAO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.User;
import org.example.shopify.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserDAO  userDAO;

    public OrderController(OrderService orderService, UserDAO userDAO) {
        this.orderService = orderService;
        this.userDAO = userDAO;
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userDAO.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(orderService.getOrdersByUserId(currentUser.getId()));
    }

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody Map<Long, Integer> items) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userDAO.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        orderService.createOrder(currentUser, items);
        return ResponseEntity.ok("Order placed successfully");
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order canceled");
    }
}
