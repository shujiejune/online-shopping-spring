package org.example.shopify.Controller;

import org.example.shopify.DTO.AdminSummaryDTO;
import org.example.shopify.DTO.OrderPageResponseDTO;
import org.example.shopify.DTO.ProductPageResponseDTO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Product;
import org.example.shopify.Service.AdminService;
import org.example.shopify.Service.OrderService;
import org.example.shopify.Service.ProductService;
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
    private final ProductService productService;

    public AdminController(AdminService adminService, OrderService orderService,  ProductService productService) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.productService = productService;
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
    public ResponseEntity<ProductPageResponseDTO> getAllProducts(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(adminService.getPaginatedProductDashboard(page));
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
        List<Product> mostPopularProducts = adminService.getMostPopularProducts(3);
        List<Product> mostProfitableProducts = adminService.getMostProfitableProducts(3);

        List<ProductResponseDTO> mostPopularProductDTOs = mapToProductDTOs(mostPopularProducts);
        List<ProductResponseDTO> mostProfitableProductDTOs = mapToProductDTOs(mostProfitableProducts);

        return ResponseEntity.ok(new AdminSummaryDTO(sold, profit, mostPopularProductDTOs, mostProfitableProductDTOs));
    }

    @GetMapping("/products/popular")
    public ResponseEntity<List<ProductResponseDTO>> getMostPopularProducts(
            @RequestParam(defaultValue = "3") int limit) {

        List<Product> products = adminService.getMostPopularProducts(limit);
        return ResponseEntity.ok(mapToProductDTOs(products));
    }

    @GetMapping("/products/profitable")
    public ResponseEntity<List<ProductResponseDTO>> getMostProfitableProducts(
            @RequestParam(defaultValue = "3") int limit) {

        List<Product> products = adminService.getMostProfitableProducts(limit);
        return ResponseEntity.ok(mapToProductDTOs(products));
    }

    private List<ProductResponseDTO> mapToProductDTOs(List<Product> products) {
        return products.stream().map(p -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setRetailPrice(p.getRetailPrice());
            return dto;
        }).collect(Collectors.toList());
    }
}
