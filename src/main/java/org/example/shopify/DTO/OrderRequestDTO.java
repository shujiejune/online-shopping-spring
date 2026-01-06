package org.example.shopify.DTO;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private List<OrderItemRequestDTO> items;

    @Data
    public static class OrderItemRequestDTO {
        private Long productId;
        private Integer quantity;
    }
}
