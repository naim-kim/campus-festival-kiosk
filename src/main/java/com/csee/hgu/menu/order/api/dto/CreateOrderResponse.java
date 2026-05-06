package com.csee.hgu.menu.order.api.dto;

public class CreateOrderResponse {
    public Long orderId;
    public Integer totalPrice;

    public CreateOrderResponse(Long orderId, Integer totalPrice) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }
}

