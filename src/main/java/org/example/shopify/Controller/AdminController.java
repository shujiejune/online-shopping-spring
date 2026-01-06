package org.example.shopify.Controller;

import org.example.shopify.Service.AdminService;
import org.example.shopify.Service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final OrderService orderService;

    public AdminController(AdminService adminService, OrderService orderService) {
        this.adminService = adminService;
        this.orderService = orderService;
    }

    @GetMapping("/stats/profit")
    public Double getProfit() {
        return adminService.getTotalProfit();
    }

    @PatchMapping("/orders/{id}/complete")
    public void complete(@PathVariable Long id) {
        orderService.completeOrder(id);
    }
}
