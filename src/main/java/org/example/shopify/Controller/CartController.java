package org.example.shopify.Controller;

import org.example.shopify.DTO.CartResponseDTO;
import org.example.shopify.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
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

    // Helper to get ID from JWT/Spring Security setup
    private Long getCurrentUserId() {
        // Placeholder: Replace with your actual security logic
        return 1L;
    }
}
