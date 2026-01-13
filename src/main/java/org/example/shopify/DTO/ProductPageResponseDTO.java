package org.example.shopify.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductPageResponseDTO {
    private List<AdminProductView> products;
    private int currentPage;
    private int totalPages;
    private long totalProducts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminProductView {
        private Long id;
        private String name;
        private Double wholesalePrice;
        private Double retailPrice;
        private Integer quantity;
    }
}
