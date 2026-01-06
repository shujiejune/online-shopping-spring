package org.example.shopify.Controller;

import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.Domain.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductDAO productDAO;

    public ProductController(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @GetMapping
    public List<Product> getAllInStockProducts() {
        return productDAO.getInStockProducts();
    }

    @GetMapping("/{id}")
    public Product getProductDetails(@PathVariable Long id) {
        return productDAO.getProductById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
