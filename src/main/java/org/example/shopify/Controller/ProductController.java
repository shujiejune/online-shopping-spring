package org.example.shopify.Controller;

import org.example.shopify.DTO.ProductPageResponseDTO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Product;
import org.example.shopify.Domain.User;
import org.example.shopify.Service.ProductService;
import org.example.shopify.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService,  UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductDetailForUser(@PathVariable Long id) {
        Product product = productService.getProductDetailForUser(id);

        ProductResponseDTO dto = mapToDTO(product);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<ProductPageResponseDTO> getInStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(productService.getPaginatedInStockProducts(page, size));
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
        return userService.getUserByUsername(username);
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
