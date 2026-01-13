package org.example.shopify.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminProductPageResponseDTO {
    private List<AdminProductResponseDTO> products;
    private int currentPage;
    private int totalPages;
    private long totalProducts;
}
