package org.example.shopify.DAO;

import org.example.shopify.Domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Optional<Product> getProductById(Long id);
    List<Product> getInStockProducts();
    List<Product> getPaginatedProducts(int page, int size);
    long getTotalProductsCount();
    void saveOrUpdateProduct(Product product);
}
