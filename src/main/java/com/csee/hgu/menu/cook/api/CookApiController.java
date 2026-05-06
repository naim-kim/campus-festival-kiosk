package com.csee.hgu.menu.cook.api;

import com.csee.hgu.menu.cook.api.dto.CookTicketDto;
import com.csee.hgu.menu.cook.api.dto.CookTicketItemDto;
import com.csee.hgu.menu.order.domain.OrderEntity;
import com.csee.hgu.menu.order.domain.OrderItemEntity;
import com.csee.hgu.menu.order.repo.OrderRepository;
import com.csee.hgu.menu.waiting.domain.WaitingStatus;
import com.csee.hgu.menu.waiting.domain.WaitingTicketEntity;
import com.csee.hgu.menu.waiting.repo.WaitingTicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cook")
public class CookApiController {

    private final WaitingTicketRepository waitingTicketRepository;
    private final OrderRepository orderRepository;

    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");

    public CookApiController(WaitingTicketRepository waitingTicketRepository, OrderRepository orderRepository) {
        this.waitingTicketRepository = waitingTicketRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/tickets")
    @Transactional(readOnly = true)
    public List<CookTicketDto> list(@RequestParam String status, @RequestParam(required = false) String date) {
        WaitingStatus st;
        try {
            st = WaitingStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid status");
        }

        LocalDate targetDate = (date == null || date.trim().isEmpty())
                ? LocalDate.now(KOREA)
                : LocalDate.parse(date.trim());
        List<WaitingTicketEntity> tickets;
        if (st == WaitingStatus.PICKED_UP) {
            tickets = waitingTicketRepository.findByBusinessDateAndStatusOrderByCompletedAtDesc(targetDate, st);
        } else {
            tickets = waitingTicketRepository.findByBusinessDateAndStatusOrderByWaitingNumberAsc(targetDate, st);
        }

        List<CookTicketDto> out = new ArrayList<>();
        for (WaitingTicketEntity t : tickets) {
            OrderEntity order = orderRepository.findWithItemsById(t.orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "order missing for ticket"));
            out.add(toDto(t, order));
        }
        return out;
    }

    @PostMapping("/tickets/{ticketId}/cooked")
    @Transactional
    public CookTicketDto cooked(@PathVariable Long ticketId) {
        WaitingTicketEntity ticket = waitingTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));

        if (ticket.status == WaitingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticket cancelled");
        }

        ticket.status = WaitingStatus.COOKED;
        ticket.completedAt = Instant.now();

        OrderEntity order = orderRepository.findWithItemsById(ticket.orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "order missing for ticket"));

        return toDto(ticket, order);
    }

    @PostMapping("/tickets/{ticketId}/picked-up")
    @Transactional
    public CookTicketDto pickedUp(@PathVariable Long ticketId) {
        WaitingTicketEntity ticket = waitingTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));

        if (ticket.status == WaitingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticket cancelled");
        }

        ticket.status = WaitingStatus.PICKED_UP;
        if (ticket.completedAt == null) {
            ticket.completedAt = Instant.now();
        }

        OrderEntity order = orderRepository.findWithItemsById(ticket.orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "order missing for ticket"));
        return toDto(ticket, order);
    }

    @PostMapping("/tickets/{ticketId}/reopen")
    @Transactional
    public CookTicketDto reopen(@PathVariable Long ticketId) {
        WaitingTicketEntity ticket = waitingTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));

        if (ticket.status == WaitingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticket cancelled");
        }

        ticket.status = WaitingStatus.CONFIRMED;
        ticket.completedAt = null;

        OrderEntity order = orderRepository.findWithItemsById(ticket.orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "order missing for ticket"));
        return toDto(ticket, order);
    }

    @GetMapping("/revenue")
    @Transactional(readOnly = true)
    public Map<String, Long> revenue() {
        LocalDate today = LocalDate.now(KOREA);
        long todayRevenue = waitingTicketRepository.sumRevenueByDateAndStatus(today, WaitingStatus.PICKED_UP);
        long totalRevenue = waitingTicketRepository.sumRevenueByStatus(WaitingStatus.PICKED_UP);
        Map<String, Long> out = new HashMap<>();
        out.put("todayRevenue", todayRevenue);
        out.put("totalRevenue", totalRevenue);
        return out;
    }

    private static CookTicketDto toDto(WaitingTicketEntity ticket, OrderEntity order) {
        CookTicketDto dto = new CookTicketDto();
        dto.ticketId = ticket.id;
        dto.orderId = order.id;
        dto.waitingNumber = ticket.waitingNumber;
        dto.status = ticket.status.name();
        dto.orderedAt = ticket.createdAt;
        dto.completedAt = ticket.completedAt;
        dto.phoneNumber = ticket.phoneNumber;
        dto.totalPrice = order.totalPrice == null ? 0 : order.totalPrice;

        dto.items = order.items.stream().map(CookApiController::toItemDto).collect(Collectors.toList());
        return dto;
    }

    private static CookTicketItemDto toItemDto(OrderItemEntity item) {
        CookTicketItemDto dto = new CookTicketItemDto();
        dto.name = item.menuName;
        dto.quantity = item.quantity == null ? 0 : item.quantity;
        dto.adeChoice = item.adeChoice;
        dto.toppings = item.toppings == null ? Collections.emptyList() : item.toppings.stream().map(t -> t.toppingName).collect(Collectors.toList());
        return dto;
    }
}

