package org.example.shopify.Controller;

import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.DTO.AdminSummaryDTO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.Product;
import org.example.shopify.Service.AdminService;
import org.example.shopify.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final OrderService orderService;
    private final ProductDAO productDAO;

    public AdminController(AdminService adminService, OrderService orderService,  ProductDAO productDAO) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.productDAO = productDAO;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrderDashboard(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(adminService.getPaginatedOrders(page));
    }

    @PatchMapping("/orders/{id}/complete")
    public void completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productDAO.getAllProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        adminService.addProduct(product);
        return ResponseEntity.ok("Product added successfully");
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        adminService.updateProduct(id, product);
        return ResponseEntity.ok("Product updated successfully");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminSummaryDTO> getDashboardStats() {
        int sold = adminService.getTotalSoldItems();
        double profit = adminService.getTotalProfit();
        List<ProductResponseDTO> top3 = adminService.getTopProducts(3);

        return ResponseEntity.ok(new AdminSummaryDTO(sold, profit, top3));
    }
}
