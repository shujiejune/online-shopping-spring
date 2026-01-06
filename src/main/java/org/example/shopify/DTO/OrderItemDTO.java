package org.example.shopify.DTO;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String productName;
    private int quantity;
    private Double purchasedPrice;
}
