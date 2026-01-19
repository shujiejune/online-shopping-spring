package org.example.shopify.DTO;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private List<OrderItemDTO> items;
}
