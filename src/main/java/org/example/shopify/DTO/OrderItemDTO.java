package org.example.shopify.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private int quantity;
    private Double purchasedPrice;
}
