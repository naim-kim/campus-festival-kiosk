package com.csee.hgu.menu.order.api.dto;

public class MarkPaidResponse {
    public Long orderId;
    public Integer waitingNumber;
    public Long waitingTicketId;

    public MarkPaidResponse(Long orderId, Integer waitingNumber, Long waitingTicketId) {
        this.orderId = orderId;
        this.waitingNumber = waitingNumber;
        this.waitingTicketId = waitingTicketId;
    }
}

