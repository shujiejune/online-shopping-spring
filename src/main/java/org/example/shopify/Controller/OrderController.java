package org.example.shopify.Controller;

import org.example.shopify.DTO.OrderPageResponseDTO;
import org.example.shopify.DTO.OrderRequestDTO;
import org.example.shopify.DTO.OrderResponseDTO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.User;
import org.example.shopify.Exception.PermissionDeniedException;
import org.example.shopify.Mapper.OrderMapper;
import org.example.shopify.Service.OrderService;
import org.example.shopify.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, UserService userService,  OrderMapper orderMapper) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderDetails(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Check if the user has ROLE_ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        System.out.println("Current user: " + currentUsername);
        System.out.println("Current admin: " + isAdmin);

        Order order = orderService.getOrderDetails(id);

        if (!order.getUser().getUsername().equals(currentUsername) && !isAdmin) {
            throw new PermissionDeniedException("You do not have permission to view this order.");
        }

        OrderResponseDTO dto = orderMapper.mapToOrderResponseDTO(order);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<OrderPageResponseDTO> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Long userId = getCurrentUserId();
        OrderPageResponseDTO response = orderService.getOrdersByUserId(userId, page, size);

        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO request) {
        Long userId = getCurrentUserId();

        // Service handles stock check, price locking, and saving
        Order newOrder = orderService.createOrder(userId, request);

        // Convert Entity to DTO
        OrderResponseDTO dto = orderMapper.mapToOrderResponseDTO(newOrder);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order canceled");
    }

    private Long getCurrentUserId() {
        // 1. Get the username from the SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.toString();

        // 2. Use the Service to get the User entity
        User user = userService.getUserByUsername(username);

        // 3. Return the ID
        return user.getId();
    }
}
