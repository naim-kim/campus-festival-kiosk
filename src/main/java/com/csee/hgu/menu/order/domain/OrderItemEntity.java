package com.csee.hgu.menu.order.domain;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    public OrderEntity order;

    @Column(nullable = false, length = 64)
    public String menuCode;

    @Column(nullable = false, length = 128)
    public String menuName;

    @Column(nullable = false)
    public Integer unitPrice;

    @Column(nullable = false)
    public Integer quantity;

    @Column(length = 64)
    public String adeChoice;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<OrderItemToppingEntity> toppings = new LinkedHashSet<>();

    public void addTopping(OrderItemToppingEntity topping) {
        topping.orderItem = this;
        toppings.add(topping);
    }
}

