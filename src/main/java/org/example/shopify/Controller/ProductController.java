package org.example.shopify.Controller;

import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DAO.UserDAO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Product;
import org.example.shopify.Domain.User;
import org.example.shopify.Exception.ResourceNotFoundException;
import org.example.shopify.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final UserDAO userDAO;

    public ProductController(ProductService productService,  UserDAO userDAO) {
        this.productService = productService;
        this.userDAO = userDAO;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);

        ProductResponseDTO dto = mapToDTO(product);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDTO>> getInStockProducts() {
        List<Product> products = productService.getInStockProducts();

        List<ProductResponseDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            ProductResponseDTO dto = mapToDTO(product);
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ProductResponseDTO>> getMostRecentlyPurchased(@RequestParam(defaultValue = "3") int limit) {
        User currentUser = getCurrentUser();

        List<Product> products = productService.getMostRecentlyPurchased(currentUser.getId(), limit);

        List<ProductResponseDTO> response = products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/frequent")
    public ResponseEntity<List<ProductResponseDTO>> getMostFrequentlyPurchased(@RequestParam(defaultValue = "3") int limit) {
        User currentUser = getCurrentUser();

        List<Product> products = productService.getMostFrequentlyPurchased(currentUser.getId(), limit);

        List<ProductResponseDTO> response = products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDAO.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ProductResponseDTO mapToDTO(Product p) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setRetailPrice(p.getRetailPrice());
        return dto;
    }
}
