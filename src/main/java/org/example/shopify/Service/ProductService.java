package org.example.shopify.Service;

import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DTO.ProductPageResponseDTO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.OrderItem;
import org.example.shopify.Domain.Product;
import org.example.shopify.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;

    public ProductService(ProductDAO productDAO, OrderDAO orderDAO) {
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
    }

    @Transactional(readOnly = true)
    public Product getProductDetailForUser(Long productId) {
        return productDAO.getProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    @Transactional(readOnly = true)
    public ProductPageResponseDTO getPaginatedInStockProducts(int page) {
        int pageSize = 10;

        List<Product> products = productDAO.getPaginatedInStockProducts(page, pageSize);
        long totalProducts = productDAO.getInStockProductsCount();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        List<ProductResponseDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setRetailPrice(p.getRetailPrice());
            dtos.add(dto);
        }

        return new ProductPageResponseDTO(dtos, page, totalPages, totalProducts);
    }

    @Transactional(readOnly = true)
    public List<Product> getMostRecentlyPurchased(Long userId, int limit) {
        List<Order> orders = orderDAO.getOrdersByUserId(userId);

        return orders.stream()
                // Sort by Date Placed (Newest first)
                .sorted((o1, o2) -> o2.getDatePlaced().compareTo(o1.getDatePlaced()))
                // Flatten: Stream<Order> -> Stream<OrderItem>
                .flatMap(order -> order.getOrderItems().stream())
                // Map: Stream<OrderItem> -> Stream<Product>
                .map(OrderItem::getProduct)
                // Remove duplicates (so we don't see "Banana pie" twice)
                .distinct()
                // Keep only the top 'limit'
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Product> getMostFrequentlyPurchased(Long userId, int limit) {
        List<Order> orders = orderDAO.getOrdersByUserId(userId);

        // Map: Product -> Total Quantity Purchased
        Map<Product, Integer> productFrequencyMap = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        Collectors.summingInt(OrderItem::getQuantity)
                ));

        // Sort the map entries by Quantity (Descending) and extract Products
        return productFrequencyMap.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Highest qty first
                .map(Map.Entry::getKey) // Extract the Product from the entry
                .limit(limit)
                .collect(Collectors.toList());
    }
}
