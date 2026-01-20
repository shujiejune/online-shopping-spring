package org.example.shopify.Service;

import org.example.shopify.DAO.OrderDAO;
import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DTO.*;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.OrderItem;
import org.example.shopify.Domain.OrderStatus;
import org.example.shopify.Domain.Product;
import org.example.shopify.Exception.ResourceNotFoundException;
import org.example.shopify.Mapper.OrderMapper;
import org.example.shopify.Mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    public AdminService(OrderDAO orderDAO,  ProductDAO productDAO,
                        OrderMapper orderMapper,  ProductMapper productMapper) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public OrderPageResponseDTO getPaginatedOrderDashboard(int page) {
        int pageSize = 5;

        List<Order> orders = orderDAO.getPaginatedOrders(page, pageSize);

        long totalOrders = orderDAO.getTotalOrdersCount();
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        List<OrderResponseDTO> dtos = orderMapper.mapToOrderResponseDTOList(orders);

        return new OrderPageResponseDTO(dtos, page, totalPages, totalOrders);
    }

    @Transactional(readOnly = true)
    public AdminProductPageResponseDTO getPaginatedProductDashboard(int page) {
        int pageSize = 10;

        List<Product> products = productDAO.getPaginatedProducts(page, pageSize);
        long totalProducts = productDAO.getTotalProductsCount();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        List<AdminProductResponseDTO> views = productMapper.mapToAdminProductDTOList(products);

        return new AdminProductPageResponseDTO(views, page, totalPages, totalProducts);
    }

    @Transactional
    public Product addProduct(ProductRequestDTO dto) {
        Product product = productMapper.mapRequestToProduct(dto);
        productDAO.saveOrUpdateProduct(product);
        return product;
    }

    @Transactional
    public Product updateProduct(ProductRequestDTO dto) {
        if (!productDAO.getProductById(dto.getId()).isPresent()) {
            throw new ResourceNotFoundException("Product not found");
        }

        Product product =  productMapper.mapRequestToProduct(dto);
        productDAO.saveOrUpdateProduct(product);

        return product;
    }

    @Transactional
    public void removeProduct(Long productId) {
        Product product = productDAO.getProductById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productDAO.saveOrUpdateProduct(product);
    }

    @Transactional(readOnly = true)
    public AdminProductResponseDTO getProductDetailForAdmin(Long productId) {
        Product p = productDAO.getProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return new AdminProductResponseDTO(
                p.getId(), p.getName(), p.getDescription(),
                p.getRetailPrice(), p.getWholesalePrice(), p.getQuantity()
        );
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
