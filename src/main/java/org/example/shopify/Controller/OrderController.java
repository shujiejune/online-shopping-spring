package org.example.shopify.Controller;

import org.example.shopify.DAO.UserDAO;
import org.example.shopify.DTO.OrderItemDTO;
import org.example.shopify.DTO.OrderResponseDTO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.Product;
import org.example.shopify.Domain.User;
import org.example.shopify.Exception.ResourceNotFoundException;
import org.example.shopify.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userDAO.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Order> orders = orderService.getOrdersByUserId(currentUser.getId());

        List<OrderResponseDTO> dtos = orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setOrderId(order.getId());
            dto.setUsername(order.getUser().getUsername());
            dto.setDatePlaced(order.getDatePlaced());
            dto.setOrderStatus(order.getOrderStatus().toString());

            List<OrderItemDTO> itemDtos = order.getOrderItems().stream().map(item -> {
                OrderItemDTO itemDto = new OrderItemDTO();
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPurchasedPrice(item.getPurchasedPrice());
                return itemDto;
            }).collect(Collectors.toList());

            dto.setItems(itemDtos);

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/recently-purchased")
    public ResponseEntity<List<ProductResponseDTO>> getRecentProducts(@RequestParam(defaultValue = "5") int limit) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDAO.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Product> products = orderService.getRecentlyPurchasedProducts(user.getId(), limit);

        List<ProductResponseDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setRetailPrice(p.getRetailPrice());
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody Map<Long, Integer> items) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userDAO.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        orderService.createOrder(currentUser, items);
        return ResponseEntity.ok("Order placed successfully");
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order canceled");
    }
}
