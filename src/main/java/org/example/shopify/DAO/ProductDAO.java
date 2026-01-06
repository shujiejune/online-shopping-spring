package org.example.shopify.DAO;

import org.example.shopify.Domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Optional<Product> getProductById(Long id);
    List<Product> getAllProducts();
    List<Product> getInStockProducts();
    void saveOrUpdateProduct(Product product);
}
