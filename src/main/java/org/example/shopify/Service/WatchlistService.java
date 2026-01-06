package org.example.shopify.Service;

import org.example.shopify.DAO.WatchlistDAO;
import org.example.shopify.Domain.Watchlist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {
    private final WatchlistDAO watchlistDAO;

    public WatchlistService(WatchlistDAO watchlistDAO) {
        this.watchlistDAO = watchlistDAO;
    }

    @Transactional
    public void addToWatchlist(Watchlist item) {
        watchlistDAO.add(item);
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long productId) {
        watchlistDAO.remove(userId, productId);
    }

    public List<Watchlist> getValidWatchlist(Long userId) {
        return watchlistDAO.getInStockWatchlist(userId);
    }
}
