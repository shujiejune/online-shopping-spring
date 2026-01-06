package org.example.shopify.Domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "purchased_price")
    private double purchasedPrice; // snapshot of the retail_price when the order was placed

    @Column(name = "wholesale_price")
    private double wholesalePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
