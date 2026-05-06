package com.csee.hgu.menu.order.domain;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    public OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Column(nullable = false)
    public Integer totalPrice;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<OrderItemEntity> items = new ArrayList<>();

    public void addItem(OrderItemEntity item) {
        item.order = this;
        items.add(item);
    }
}

