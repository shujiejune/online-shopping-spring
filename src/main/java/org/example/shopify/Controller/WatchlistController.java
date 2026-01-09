package org.example.shopify.Controller;

import org.example.shopify.DAO.UserDAO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Product;
import org.example.shopify.Domain.User;
import org.example.shopify.Domain.Watchlist;
import org.example.shopify.Service.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;
    private final UserDAO userDAO;

    public WatchlistController(WatchlistService watchlistService, UserDAO userDAO) {
        this.watchlistService = watchlistService;
        this.userDAO = userDAO;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addToWatchlist(@PathVariable Long productId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDAO.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        watchlistService.addToWatchlist(user.getId(), productId);
        return ResponseEntity.ok("Product added to watchlist");
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromWatchlist(@PathVariable Long productId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDAO.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        watchlistService.removeFromWatchlist(user.getId(), productId);
        return ResponseEntity.ok("Product removed from watchlist");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getMyWatchlist() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDAO.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Get the list of Watchlist entities from the service
        List<Watchlist> watchlistEntries = watchlistService.getInStockWatchlist(user.getId());

        // 2. Map Watchlist -> Product -> ProductResponseDTO
        List<ProductResponseDTO> dtos = new ArrayList<>();
        for (Watchlist entry : watchlistEntries) {
            Product p = entry.getProduct();
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setRetailPrice(p.getRetailPrice());
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }
}
