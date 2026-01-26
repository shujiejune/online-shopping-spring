package org.example.shopify.DAO;

import org.example.shopify.Domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Optional<Product> getProductById(Long id);
    List<Product> getPaginatedInStockProducts(int page, int size);
    List<Product> getPaginatedProducts(int page, int size);
    long getTotalProductsCount();
    long getInStockProductsCount();
    Product saveOrUpdateProduct(Product product);
    void deleteProduct(Product product);
}
