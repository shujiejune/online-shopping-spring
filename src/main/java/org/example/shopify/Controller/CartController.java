package org.example.shopify.Controller;

import org.example.shopify.DTO.CartResponseDTO;
import org.example.shopify.DTO.OrderResponseDTO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.User;
import org.example.shopify.Service.CartService;
import org.example.shopify.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getMyCart() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<String> addItem(@RequestParam Long productId, @RequestParam Integer quantity) {
        cartService.addToCart(getCurrentUserId(), productId, quantity);
        return ResponseEntity.ok("Item added to cart");
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<String> updateQuantity(@PathVariable Long itemId, @RequestParam Integer quantity) {
        cartService.updateItemQuantity(itemId, quantity);
        return ResponseEntity.ok("Quantity updated");
    }

    // Remove a single item
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long itemId) {
        cartService.removeItemFromCart(itemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    // Clear everything
    @DeleteMapping
    public ResponseEntity<String> clearCart() {
        cartService.clearUserCart(getCurrentUserId());
        return ResponseEntity.ok("Cart cleared");
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout() {
        Long userId = getCurrentUserId();

        // 1. Service returns the "Source of Truth" (Entity)
        CartResponseDTO cartDTO = cartService.getCart(userId);
        Order newOrder = cartService.checkout(userId);

        // 2. Controller converts it to the "View" (DTO)
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(newOrder.getId());
        response.setOrderStatus(newOrder.getOrderStatus().toString());
        response.setTotalAmount(cartDTO.getTotalPrice());

        return ResponseEntity.ok(response);
    }

    // Helper to get ID from JWT/Spring Security setup
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
