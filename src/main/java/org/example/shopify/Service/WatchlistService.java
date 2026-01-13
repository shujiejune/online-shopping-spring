package org.example.shopify.Service;

import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DAO.UserDAO;
import org.example.shopify.DAO.WatchlistDAO;
import org.example.shopify.Domain.Product;
import org.example.shopify.Domain.User;
import org.example.shopify.Domain.Watchlist;
import org.example.shopify.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {
    private final WatchlistDAO watchlistDAO;
    private final UserDAO userDAO;
    private final ProductDAO productDAO;

    public WatchlistService(WatchlistDAO watchlistDAO,  UserDAO userDAO, ProductDAO productDAO) {
        this.watchlistDAO = watchlistDAO;
        this.userDAO = userDAO;
        this.productDAO = productDAO;
    }

    @Transactional
    public void addToWatchlist(Long userId, Long productId) {
        User user = userDAO.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productDAO.getProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getQuantity() <= 0) {
            throw new IllegalStateException("Cannot add out-of-stock product to watchlist.");
        }

        if (watchlistDAO.exists(userId, productId)) {
            throw new IllegalStateException("This product is already added into watchlist.");
        }

        Watchlist item = new Watchlist();
        item.setUser(user);
        item.setProduct(product);

        watchlistDAO.add(item);
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long productId) {
        watchlistDAO.remove(userId, productId);
    }

    public List<Watchlist> getInStockWatchlist(Long userId) {
        return watchlistDAO.getInStockWatchlist(userId);
    }
}
