package org.example.shopify.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminSummaryDTO {
    private double totalProfit;
    private int totalItemsSold;
}