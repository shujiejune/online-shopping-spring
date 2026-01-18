package org.example.shopify.DAO;

import org.example.shopify.Domain.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartDAO {
    Optional<CartItem> getCartItemById(Long id);
    List<CartItem> getCartByUserId(Long userId);
    void addItemToCart(CartItem cartItem);
    void updateQuantity(Long itemId, Integer quantity);
    void removeItem(Long itemId);
    void clearCart(Long userId);
    Optional<CartItem> findExistingItem(Long userId, Long productId);
}
