package org.example.shopify.Controller;

import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductDAO productDAO;

    public ProductController(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Product> products = productDAO.getInStockProducts();
        List<ProductResponseDTO> dtos = new ArrayList<>();

        for (Product p : products) {
            dtos.add(mapToDTO(p));
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductDetail(@PathVariable Long id) {
        return productDAO.getProductById(id)
                .map(product -> ResponseEntity.ok(mapToDTO(product)))
                .orElse(ResponseEntity.notFound().build());
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
