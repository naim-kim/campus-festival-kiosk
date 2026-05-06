package com.csee.hgu.menu.waiting.domain;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "waiting_tickets",
        uniqueConstraints = @UniqueConstraint(name = "uk_waiting_date_number", columnNames = {"business_date", "waiting_number"})
)
public class WaitingTicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "order_id", nullable = false)
    public Long orderId;

    @Column(name = "business_date", nullable = false)
    public LocalDate businessDate;

    @Column(name = "waiting_number", nullable = false)
    public Integer waitingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    public WaitingStatus status = WaitingStatus.ISSUED;

    @Column(length = 20)
    public String phoneNumber;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();

    public Instant completedAt;
}

