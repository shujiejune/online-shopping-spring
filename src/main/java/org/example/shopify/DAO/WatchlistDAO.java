package org.example.shopify.DAO;

import org.example.shopify.Domain.Watchlist;

import java.util.List;

public interface WatchlistDAO {
    List<Watchlist> getInStockWatchlist(Long userId);
    void add(Watchlist item);
    void remove(Long userId, Long productId);
}
