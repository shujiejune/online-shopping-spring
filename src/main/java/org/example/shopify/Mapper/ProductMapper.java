package org.example.shopify.Mapper;

import org.example.shopify.DTO.AdminProductResponseDTO;
import org.example.shopify.DTO.ProductRequestDTO;
import org.example.shopify.DTO.ProductResponseDTO;
import org.example.shopify.Domain.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    public Product mapRequestToProduct(ProductRequestDTO dto) {
        Product product = new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setWholesalePrice(dto.getWholesalePrice());
        product.setRetailPrice(dto.getRetailPrice());
        product.setQuantity(dto.getQuantity());

        return product;
    }

    public ProductResponseDTO mapToProductResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setRetailPrice(product.getRetailPrice());

        return dto;
    }

    public AdminProductResponseDTO mapToAdminProductResponseDTO(Product product) {
        AdminProductResponseDTO dto = new AdminProductResponseDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setRetailPrice(product.getRetailPrice());
        dto.setWholesalePrice(product.getWholesalePrice());
        dto.setQuantity(product.getQuantity());

        return dto;
    }

    public List<ProductResponseDTO> mapToProductDTOList(List<Product> products) {
        return products.stream().map(this::mapToProductResponseDTO).collect(Collectors.toList());
    }

    public List<AdminProductResponseDTO> mapToAdminProductDTOList(List<Product> products) {
        return products.stream().map(this::mapToAdminProductResponseDTO).collect(Collectors.toList());
    }
}
