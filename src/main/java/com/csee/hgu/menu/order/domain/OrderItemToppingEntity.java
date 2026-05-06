package com.csee.hgu.menu.order.domain;

import javax.persistence.*;

@Entity
@Table(name = "order_item_toppings")
public class OrderItemToppingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    public OrderItemEntity orderItem;

    @Column(nullable = false, length = 64)
    public String toppingName;

    public OrderItemToppingEntity() {}

    public OrderItemToppingEntity(String toppingName) {
        this.toppingName = toppingName;
    }
}

