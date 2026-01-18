package org.example.shopify.Controller;

import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Product;
import org.example.shopify.Domain.User;
import org.example.shopify.Domain.Watchlist;
import org.example.shopify.Service.UserService;
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
    private final UserService userService;

    public WatchlistController(WatchlistService watchlistService, UserService userService) {
        this.watchlistService = watchlistService;
        this.userService = userService;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addToWatchlist(@PathVariable Long productId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByUsername(username);
        watchlistService.addToWatchlist(user.getId(), productId);
        return ResponseEntity.ok("Product added to watchlist");
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromWatchlist(@PathVariable Long productId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByUsername(username);
        watchlistService.removeFromWatchlist(user.getId(), productId);
        return ResponseEntity.ok("Product removed from watchlist");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getMyWatchlist() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByUsername(username);

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
