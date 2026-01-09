package org.example.shopify.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminSummaryDTO {
    private int totalItemsSold;
    private double totalProfit;
    private List<ProductResponseDTO> topProducts;
}