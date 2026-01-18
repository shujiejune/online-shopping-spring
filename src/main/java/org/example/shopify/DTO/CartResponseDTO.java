package org.example.shopify.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private List<CartItemDTO> items;
    private Double totalPrice;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        private Long itemId;
        private Long productId;
        private String productName;
        private Double retailPrice;
        private Integer quantity;
        private Double subtotal; // (Price * Quantity)
    }
}
