package org.example.shopify.Service;

import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DTO.OrderPageResponseDTO;
import org.example.shopify.DTO.OrderResponseDTO;
import org.example.shopify.DTO.ProductPageResponseDTO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.OrderItem;
import org.example.shopify.Domain.OrderStatus;
import org.example.shopify.Domain.Product;
import org.example.shopify.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    public AdminService(OrderDAO orderDAO,  ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    @Transactional(readOnly = true)
    public OrderPageResponseDTO getPaginatedOrderDashboard(int page) {
        int pageSize = 5;

        List<Order> orders = orderDAO.getPaginatedOrders(page, pageSize);

        long totalOrders = orderDAO.getTotalOrdersCount();
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        List<OrderResponseDTO> dtos = new ArrayList<>();
        for (Order o : orders) {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setOrderId(o.getId());
            dto.setUsername(o.getUser().getUsername());
            dto.setDatePlaced(o.getDatePlaced());
            dto.setOrderStatus(o.getOrderStatus().toString());

            // Leave dto.setItems(null) here because the dashboard list
            // shouldn't show all products until the admin clicks a specific order.
            dtos.add(dto);
        }

        return new OrderPageResponseDTO(dtos, page, totalPages, totalOrders);
    }

    @Transactional(readOnly = true)
    public ProductPageResponseDTO getPaginatedProductDashboard(int page) {
        int pageSize = 10;

        List<Product> products = productDAO.getPaginatedProducts(page, pageSize);
        long totalProducts = productDAO.getTotalProductsCount();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        List<ProductPageResponseDTO.AdminProductView> views = new ArrayList<>();
        for (Product p : products) {
            ProductPageResponseDTO.AdminProductView view = new ProductPageResponseDTO.AdminProductView();
            view.setId(p.getId());
            view.setName(p.getName());
            view.setWholesalePrice(p.getWholesalePrice());
            view.setRetailPrice(p.getRetailPrice());
            view.setQuantity(p.getQuantity());
            views.add(view);
        }

        return new ProductPageResponseDTO(views, page, totalPages, totalProducts);
    }

    @Transactional
    public void addProduct(Product product) {
        productDAO.saveOrUpdateProduct(product);
    }

    @Transactional
    public void updateProduct(Long id, Product updatedData) {
        Product existing = productDAO.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setWholesalePrice(updatedData.getWholesalePrice());
        existing.setRetailPrice(updatedData.getRetailPrice());
        existing.setQuantity(updatedData.getQuantity());

        productDAO.saveOrUpdateProduct(existing);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<Product> getMostPopularProducts(int limit) {
        return orderDAO.getMostPopularProducts(limit);
    }

    @Transactional(readOnly = true)
    public List<Product> getMostProfitableProducts(int limit) {
        return orderDAO.getMostProfitableProducts(limit);
    }
}
