package com.csee.hgu.menu.waiting.api;

import com.csee.hgu.menu.waiting.api.dto.ConfirmWaitingRequest;
import com.csee.hgu.menu.waiting.domain.WaitingStatus;
import com.csee.hgu.menu.waiting.domain.WaitingTicketEntity;
import com.csee.hgu.menu.waiting.repo.WaitingTicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api")
public class WaitingApiController {

    private final WaitingTicketRepository waitingTicketRepository;
    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");

    public WaitingApiController(WaitingTicketRepository waitingTicketRepository) {
        this.waitingTicketRepository = waitingTicketRepository;
    }

    @PostMapping("/waiting/{ticketId}/confirm")
    @Transactional
    public WaitingTicketEntity confirm(@PathVariable Long ticketId, @RequestBody(required = false) ConfirmWaitingRequest req) {
        WaitingTicketEntity ticket = waitingTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));

        if (ticket.status == WaitingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticket cancelled");
        }

        if (req != null && req.phoneNumber != null) {
            String phone = req.phoneNumber.trim();
            if (!phone.isEmpty() && phone.length() > 20) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "phoneNumber too long");
            }
            ticket.phoneNumber = phone.isEmpty() ? null : phone;
        }

        ticket.status = WaitingStatus.CONFIRMED;
        return ticket;
    }

    @GetMapping("/waiting/pending-count")
    public long pendingCount() {
        return waitingTicketRepository.countByBusinessDateAndStatus(LocalDate.now(KOREA), WaitingStatus.CONFIRMED);
    }
}

