package com.csee.hgu.menu.cook.api.dto;

import java.time.Instant;
import java.util.List;

public class CookTicketDto {
    public Long ticketId;
    public Long orderId;
    public int waitingNumber;
    public String status;
    public Instant orderedAt;
    public Instant completedAt;
    public String phoneNumber;
    public int totalPrice;
    public List<CookTicketItemDto> items;
}

