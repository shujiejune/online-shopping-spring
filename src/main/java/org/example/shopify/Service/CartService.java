package org.example.shopify.Service;

import org.example.shopify.DAO.CartDAO;
import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DAO.UserDAO;
import org.example.shopify.DTO.CartResponseDTO;
import org.example.shopify.Domain.*;
import org.example.shopify.Exception.EmptyCartCheckoutException;
import org.example.shopify.Exception.NotEnoughInventoryException;
import org.example.shopify.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;
    private final UserDAO userDAO;
    private final OrderDAO orderDAO;

    public CartService(CartDAO cartDAO, ProductDAO productDAO, UserDAO userDAO,  OrderDAO orderDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
        this.userDAO = userDAO;
        this.orderDAO = orderDAO;
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

    @Transactional
    public Order checkout(Long userId) {
        // 1. Get all items in the user's cart
        List<CartItem> cartItems = cartDAO.getCartByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new EmptyCartCheckoutException("Cannot checkout an empty cart.");
        }

        // 2. Create the Order entity
        User user = userDAO.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setDatePlaced(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.Completed);  // or Completed after payment

        List<OrderItem> orderItems = new ArrayList<>();

        // 3. Convert CartItems to OrderItems and Update Stock
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Final stock check
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new NotEnoughInventoryException("Product " + product.getName() + " is out of stock.");
            }

            // Reduce inventory
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productDAO.saveOrUpdateProduct(product);

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPurchasedPrice(product.getRetailPrice());
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);

        // 4. Save the order and clear the cart
        orderDAO.saveOrder(order);
        cartDAO.clearCart(userId);

        return order;
    }
}
