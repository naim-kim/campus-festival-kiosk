package com.csee.hgu.menu.order.api.dto;

import java.util.List;

public class CreateOrderItemRequest {
    public String menuCode; // ex) set_basic, jeon_basic, ade_green
    public String menuName; // snapshot (display name)
    public Integer unitPrice; // snapshot (won)
    public Integer quantity;
    public String adeChoice; // optional (ex: "청포도 에이드")
    public List<String> toppings; // optional
}

