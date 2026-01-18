package org.example.shopify.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private String username;
    private LocalDateTime datePlaced;
    private Double totalAmount;
    private String orderStatus;
    private List<OrderItemDTO> items;
}
