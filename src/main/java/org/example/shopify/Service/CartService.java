package org.example.shopify.Service;

import org.example.shopify.DAO.CartDAO;
import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DAO.UserDAO;
import org.example.shopify.DTO.CartResponseDTO;
import org.example.shopify.Domain.CartItem;
import org.example.shopify.Domain.Product;
import org.example.shopify.Domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;
    private final UserDAO userDAO;

    public CartService(CartDAO cartDAO, ProductDAO productDAO, UserDAO userDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
        this.userDAO = userDAO;
    }

    @Transactional
    public void addToCart(Long userId, Long productId, Integer quantity) {
        // 1. Validate Product Stock
        Product product = productDAO.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // 2. Check if item already exists in user's cart
        Optional<CartItem> existingItem = cartDAO.findExistingItem(userId, productId);
        if (existingItem.isPresent()) {
            cartDAO.updateQuantity(existingItem.get().getId(), existingItem.get().getQuantity() + quantity);
        } else {
            User user = userDAO.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartDAO.addItemToCart(newItem);
        }
    }

    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Long userId) {
        List<CartItem> items = cartDAO.getCartByUserId(userId);

        double totalCartPrice = 0.0;
        List<CartResponseDTO.CartItemDTO> dtos = new ArrayList<>();

        for (CartItem item : items) {
            double subtotal = item.getProduct().getRetailPrice() * item.getQuantity();
            totalCartPrice += subtotal;

            dtos.add(new CartResponseDTO.CartItemDTO(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getProduct().getRetailPrice(),
                    item.getQuantity(),
                    subtotal
            ));
        }

        return new CartResponseDTO(dtos, totalCartPrice);
    }

    @Transactional
    public void updateItemQuantity(Long itemId, Integer requestedQuantity) {
        // 1. Fetch with Optional
        CartItem item = cartDAO.getCartItemById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // 2. If quantity is updated to 0 or less, just remove it
        if (requestedQuantity <= 0) {
            cartDAO.removeItem(itemId);
            return;
        }

        // 3. Stock check
        if (item.getProduct().getQuantity() < requestedQuantity) {
            throw new RuntimeException("Insufficient stock");
        }

        cartDAO.updateQuantity(itemId, requestedQuantity);
    }

    @Transactional
    public void removeItemFromCart(Long itemId) {
        cartDAO.removeItem(itemId);
    }

    @Transactional
    public void clearUserCart(Long userId) {
        userDAO.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartDAO.clearCart(userId);
    }
}
