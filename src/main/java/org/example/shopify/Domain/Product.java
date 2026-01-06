package org.example.shopify.Domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "retail_price")
    private double retailPrice; // current price displayed to users

    @Column(name = "wholesale_price")
    private double wholesalePrice; // the cost admin paid to buy this product from manufacturer
}
