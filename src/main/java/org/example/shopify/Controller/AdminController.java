package org.example.shopify.Controller;

import org.example.shopify.DTO.*;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.Product;
import org.example.shopify.Mapper.OrderMapper;
import org.example.shopify.Mapper.ProductMapper;
import org.example.shopify.Service.AdminService;
import org.example.shopify.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    public AdminController(AdminService adminService, OrderService orderService,
                           OrderMapper orderMapper,  ProductMapper productMapper) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @GetMapping("/orders")
    public ResponseEntity<OrderPageResponseDTO> getAllOrders(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(adminService.getPaginatedOrderDashboard(page));
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(orderMapper.mapToOrderResponseDTO(order));
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<OrderResponseDTO> completeOrder(@PathVariable Long id) {
        Order order = orderService.completeOrder(id);
        return ResponseEntity.ok(orderMapper.mapToOrderResponseDTO(order));
    }

    @GetMapping("/products")
    public ResponseEntity<AdminProductPageResponseDTO> getAllProducts(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(adminService.getPaginatedProductDashboard(page));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<AdminProductResponseDTO> getProductDetail(@PathVariable Long id) {
        AdminProductResponseDTO dto = adminService.getProductDetailForAdmin(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/products")
    public ResponseEntity<AdminProductResponseDTO> addProduct(@RequestBody ProductRequestDTO request) {
        Product product = adminService.addProduct(request);
        AdminProductResponseDTO dto = productMapper.mapToAdminProductResponseDTO(product);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<AdminProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO request) {
        request.setId(id);
        Product product = adminService.updateProduct(request);
        AdminProductResponseDTO dto = productMapper.mapToAdminProductResponseDTO(product);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        adminService.removeProduct(id);
        return ResponseEntity.ok("Product removed successfully");
    }

    @GetMapping("/stats/sold-count")
    public ResponseEntity<Integer> getSoldCount() {
        return ResponseEntity.ok(adminService.getTotalSoldItems());
    }

    @GetMapping("/stats/profit")
    public ResponseEntity<Double> getTotalProfit() {
        return ResponseEntity.ok(adminService.getTotalProfit());
    }

    @GetMapping("/products/popular")
    public ResponseEntity<List<AdminProductResponseDTO>> getMostPopularProducts(
            @RequestParam(defaultValue = "3") int limit) {
        List<Product> products = adminService.getMostPopularProducts(limit);
        List<AdminProductResponseDTO> dtos = productMapper.mapToAdminProductDTOList(products);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/products/profitable")
    public ResponseEntity<List<AdminProductResponseDTO>> getMostProfitableProducts(
            @RequestParam(defaultValue = "3") int limit) {
        List<Product> products = adminService.getMostProfitableProducts(limit);
        List<AdminProductResponseDTO> dtos = productMapper.mapToAdminProductDTOList(products);
        return ResponseEntity.ok(dtos);
    }
}
