package org.example.shopify.DTO;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double retailPrice;
}
