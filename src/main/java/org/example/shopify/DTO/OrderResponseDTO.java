package org.example.shopify.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private LocalDateTime datePlaced;
    private String orderStatus;
    private List<OrderItemDTO> items;
}
