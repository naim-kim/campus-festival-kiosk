package com.csee.hgu.menu.order.api;

import com.csee.hgu.menu.order.api.dto.CreateOrderItemRequest;
import com.csee.hgu.menu.order.api.dto.CreateOrderRequest;
import com.csee.hgu.menu.order.api.dto.CreateOrderResponse;
import com.csee.hgu.menu.order.api.dto.MarkPaidResponse;
import com.csee.hgu.menu.order.domain.OrderEntity;
import com.csee.hgu.menu.order.domain.OrderItemEntity;
import com.csee.hgu.menu.order.domain.OrderItemToppingEntity;
import com.csee.hgu.menu.order.domain.OrderStatus;
import com.csee.hgu.menu.order.repo.OrderRepository;
import com.csee.hgu.menu.waiting.domain.WaitingTicketEntity;
import com.csee.hgu.menu.waiting.repo.WaitingTicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class OrderApiController {

    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");

    private final OrderRepository orderRepository;
    private final WaitingTicketRepository waitingTicketRepository;

    public OrderApiController(OrderRepository orderRepository, WaitingTicketRepository waitingTicketRepository) {
        this.orderRepository = orderRepository;
        this.waitingTicketRepository = waitingTicketRepository;
    }

    @PostMapping("/orders")
    @Transactional
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        if (request == null || request.items == null || request.items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items is required");
        }

        int total = 0;
        OrderEntity order = new OrderEntity();
        order.status = OrderStatus.PENDING_PAYMENT;

        for (CreateOrderItemRequest itemReq : request.items) {
            validateItem(itemReq);
            OrderItemEntity item = new OrderItemEntity();
            item.menuCode = itemReq.menuCode;
            item.menuName = itemReq.menuName;
            item.unitPrice = itemReq.unitPrice;
            item.quantity = itemReq.quantity;
            item.adeChoice = itemReq.adeChoice;
            if (itemReq.toppings != null) {
                for (String t : itemReq.toppings) {
                    if (t != null && !t.trim().isEmpty()) {
                        item.addTopping(new OrderItemToppingEntity(t.trim()));
                    }
                }
            }
            order.addItem(item);
            total += itemReq.unitPrice * itemReq.quantity;
        }

        order.totalPrice = total;
        OrderEntity saved = orderRepository.save(order);
        return new CreateOrderResponse(saved.id, saved.totalPrice);
    }

    @PostMapping("/orders/{orderId}/paid")
    @Transactional
    public MarkPaidResponse markPaid(@PathVariable Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));

        if (order.status == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "order cancelled");
        }

        order.status = OrderStatus.PAID;

        LocalDate businessDate = LocalDate.now(KOREA);
        int next = waitingTicketRepository.findMaxWaitingNumberForDate(businessDate) + 1;

        WaitingTicketEntity ticket = new WaitingTicketEntity();
        ticket.orderId = order.id;
        ticket.businessDate = businessDate;
        ticket.waitingNumber = next;
        ticket.status = com.csee.hgu.menu.waiting.domain.WaitingStatus.CONFIRMED;
        WaitingTicketEntity savedTicket = waitingTicketRepository.save(ticket);

        return new MarkPaidResponse(order.id, savedTicket.waitingNumber, savedTicket.id);
    }

    @GetMapping("/orders/{orderId}")
    public OrderEntity getOrder(@PathVariable Long orderId) {
        // simple debug endpoint for Swagger/testing
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));
    }

    @GetMapping("/waiting/current")
    public int currentWaitingNumber() {
        return waitingTicketRepository.findMaxWaitingNumberForDate(LocalDate.now(KOREA));
    }

    private static void validateItem(CreateOrderItemRequest item) {
        if (item == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item is required");
        if (item.menuCode == null || item.menuCode.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "menuCode is required");
        if (item.menuName == null || item.menuName.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "menuName is required");
        if (item.unitPrice == null || item.unitPrice < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unitPrice is invalid");
        if (item.quantity == null || item.quantity <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity is invalid");
        if (item.toppings != null && item.toppings.size() > 10)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "too many toppings");
        if (item.adeChoice != null && item.adeChoice.length() > 64)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "adeChoice too long");
    }
}

