package org.example.shopify.Controller;

import org.example.shopify.DTO.*;
import org.example.shopify.Domain.Product;
import org.example.shopify.Service.AdminService;
import org.example.shopify.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final OrderService orderService;

    public AdminController(AdminService adminService, OrderService orderService) {
        this.adminService = adminService;
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<OrderPageResponseDTO> getAllOrders(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(adminService.getPaginatedOrderDashboard(page));
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order has been cancelled and inventory restocked.");
    }

    @PutMapping("/orders/{id}/complete")
    public void completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
    }

    @GetMapping("/products")
    public ResponseEntity<AdminProductPageResponseDTO> getAllProducts(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(adminService.getPaginatedProductDashboard(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminProductResponseDTO> getProductDetail(@PathVariable Long id) {
        AdminProductResponseDTO dto = adminService.getProductDetailForAdmin(id);
        return ResponseEntity.ok(dto);
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
        return ResponseEntity.ok(mapToAdminProductDTOs(products));
    }

    @GetMapping("/products/profitable")
    public ResponseEntity<List<AdminProductResponseDTO>> getMostProfitableProducts(
            @RequestParam(defaultValue = "3") int limit) {

        List<Product> products = adminService.getMostProfitableProducts(limit);
        return ResponseEntity.ok(mapToAdminProductDTOs(products));
    }

    private List<AdminProductResponseDTO> mapToAdminProductDTOs(List<Product> products) {
        return products.stream().map(p -> {
            AdminProductResponseDTO dto = new AdminProductResponseDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setRetailPrice(p.getRetailPrice());
            dto.setWholesalePrice(p.getWholesalePrice());
            dto.setQuantity(p.getQuantity());
            return dto;
        }).collect(Collectors.toList());
    }
}
