package org.example.shopify.Controller;

import org.example.shopify.DTO.*;
import org.example.shopify.Domain.Order;
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
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(mapToOrderDTO(order));
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<OrderResponseDTO> completeOrder(@PathVariable Long id) {
        Order order = orderService.completeOrder(id);
        return ResponseEntity.ok(mapToOrderDTO(order));
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
        return ResponseEntity.ok(mapToAdminProductDTO(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<AdminProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO request) {
        request.setId(id);
        Product product = adminService.updateProduct(request);
        return ResponseEntity.ok(mapToAdminProductDTO(product));
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
        return ResponseEntity.ok(mapToAdminProductDTOs(products));
    }

    @GetMapping("/products/profitable")
    public ResponseEntity<List<AdminProductResponseDTO>> getMostProfitableProducts(
            @RequestParam(defaultValue = "3") int limit) {

        List<Product> products = adminService.getMostProfitableProducts(limit);
        return ResponseEntity.ok(mapToAdminProductDTOs(products));
    }

    private OrderResponseDTO mapToOrderDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setUsername(order.getUser().getUsername());
        dto.setDatePlaced(order.getDatePlaced());
        dto.setOrderStatus(order.getOrderStatus().toString());
        dto.setTotalAmount(order.getTotalAmount());
        List<OrderItemDTO> itemDtos = mapToItemDTOs(order);

        dto.setItems(itemDtos);

        return dto;
    }

    private List<OrderItemDTO> mapToItemDTOs(Order order) {
        return order.getOrderItems().stream().map(item -> {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setOrderItemId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPurchasedPrice(item.getPurchasedPrice());
            return itemDto;
        }).collect(Collectors.toList());
    }

    private AdminProductResponseDTO mapToAdminProductDTO(Product product) {
        AdminProductResponseDTO dto = new AdminProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setRetailPrice(product.getRetailPrice());
        dto.setWholesalePrice(product.getWholesalePrice());
        dto.setQuantity(product.getQuantity());
        return dto;
    }

    private List<AdminProductResponseDTO> mapToAdminProductDTOs(List<Product> products) {
        return products.stream().map(this::mapToAdminProductDTO).collect(Collectors.toList());
    }
}
