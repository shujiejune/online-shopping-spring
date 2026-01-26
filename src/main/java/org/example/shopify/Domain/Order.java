package org.example.shopify.Domain;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "date_placed")
    private LocalDateTime datePlaced;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "total_amount")
    private Double totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        if (this.orderItems != null && !this.orderItems.isEmpty()) {
            this.totalAmount = this.orderItems.stream()
                    .mapToDouble(item -> item.getPurchasedPrice() * item.getQuantity())
                    .sum();
        } else {
            this.totalAmount = 0.0;
        }
    }
}
