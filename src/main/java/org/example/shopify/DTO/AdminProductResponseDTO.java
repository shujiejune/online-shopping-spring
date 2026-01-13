package org.example.shopify.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double retailPrice;
    private Double wholesalePrice; // Admin only
    private Integer quantity;      // Admin only
}
