package org.example.shopify.Service;

import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.OrderItem;
import org.example.shopify.Domain.OrderStatus;
import org.example.shopify.Domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    public AdminService(OrderDAO orderDAO,  ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    public List<Order> getPaginatedOrders(int page) {
        int pageSize = 5;
        // Your OrderDAO should implement this using setFirstResult and setMaxResults
        return orderDAO.getPaginatedOrders(page, pageSize);
    }

    @Transactional
    public void addProduct(Product product) {
        productDAO.saveOrUpdateProduct(product);
    }

    @Transactional
    public void updateProduct(Long id, Product updatedData) {
        Product existing = productDAO.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setDescription(updatedData.getDescription());
        existing.setWholesalePrice(updatedData.getWholesalePrice());
        existing.setRetailPrice(updatedData.getRetailPrice());
        existing.setQuantity(updatedData.getQuantity());

        productDAO.saveOrUpdateProduct(existing);
    }

    public int getTotalSoldItems() {
        List<Order> orders = orderDAO.getAllOrders();
        int totalSum = 0;

        for (Order order : orders) {
            if (order.getOrderStatus() == OrderStatus.Completed) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    totalSum += orderItem.getQuantity();
                }
            }
        }

        return totalSum;
    }

    public double getTotalProfit() {
        List<Order> orders = orderDAO.getAllOrders();
        double totalProfit = 0;

        for (Order order : orders) {
            if (order.getOrderStatus() == OrderStatus.Completed) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    totalProfit += orderItem.getQuantity() * (orderItem.getPurchasedPrice() - orderItem.getWholesalePrice());
                }
            }
        }

        return totalProfit;
    }

    public List<ProductResponseDTO> getTopProducts(int limit) {
        List<Order> allOrders = orderDAO.getAllOrders();
        Map<Product, Integer> counts = new HashMap<>();

        for (Order order : allOrders) {
            if (order.getOrderStatus() == OrderStatus.Completed) {
                for (OrderItem item : order.getOrderItems()) {
                    Product p = item.getProduct();
                    counts.put(p, counts.getOrDefault(p, 0) + item.getQuantity());
                }
            }
        }

        List<Map.Entry<Product, Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Take the top X and convert to DTOs
        List<ProductResponseDTO> topProducts = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, list.size()); i++) {
            Product p = list.get(i).getKey();
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setRetailPrice(p.getRetailPrice());
            topProducts.add(dto);
        }
        return topProducts;
    }
}
