package org.example.shopify.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderPageResponseDTO {
    private List<OrderResponseDTO> orders;
    private int currentPage;
    private int totalPages;
    private long totalOrders;
}
