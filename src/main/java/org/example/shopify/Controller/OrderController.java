package org.example.shopify.Controller;

import org.example.shopify.DTO.OrderItemDTO;
import org.example.shopify.DTO.OrderResponseDTO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.User;
import org.example.shopify.Exception.PermissionDeniedException;
import org.example.shopify.Service.OrderService;
import org.example.shopify.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
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

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setDatePlaced(order.getDatePlaced());
        dto.setOrderStatus(order.getOrderStatus().toString());
        dto.setUsername(order.getUser().getUsername());

        List<OrderItemDTO> itemDtos = mapToItemDTOs(order);

        dto.setItems(itemDtos);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.getUserByUsername(username);

        List<Order> orders = orderService.getOrdersByUserId(currentUser.getId());

        List<OrderResponseDTO> dtos = orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setOrderId(order.getId());
            dto.setUsername(order.getUser().getUsername());
            dto.setDatePlaced(order.getDatePlaced());
            dto.setOrderStatus(order.getOrderStatus().toString());

            List<OrderItemDTO> itemDtos = mapToItemDTOs(order);

            dto.setItems(itemDtos);

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody Map<Long, Integer> items) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.getUserByUsername(username);
        orderService.createOrder(currentUser, items);
        return ResponseEntity.ok("Order placed successfully");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order canceled");
    }

    private List<OrderItemDTO> mapToItemDTOs(Order order) {
        return order.getOrderItems().stream().map(item -> {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPurchasedPrice(item.getPurchasedPrice());
            return itemDto;
        }).collect(Collectors.toList());
    }
}
