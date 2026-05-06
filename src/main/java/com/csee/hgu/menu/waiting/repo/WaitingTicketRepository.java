package com.csee.hgu.menu.waiting.repo;

import com.csee.hgu.menu.waiting.domain.WaitingTicketEntity;
import com.csee.hgu.menu.waiting.domain.WaitingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.time.LocalDate;

public interface WaitingTicketRepository extends JpaRepository<WaitingTicketEntity, Long> {

    @Query("select coalesce(max(w.waitingNumber), 0) from WaitingTicketEntity w where w.businessDate = ?1")
    int findMaxWaitingNumberForDate(LocalDate businessDate);

    List<WaitingTicketEntity> findByBusinessDateAndStatusOrderByWaitingNumberAsc(LocalDate businessDate, WaitingStatus status);

    List<WaitingTicketEntity> findByBusinessDateAndStatusOrderByCompletedAtDesc(LocalDate businessDate, WaitingStatus status);

    long countByBusinessDateAndStatus(LocalDate businessDate, WaitingStatus status);

    long countByBusinessDateAndStatusNot(LocalDate businessDate, WaitingStatus status);

    @Query("select coalesce(sum(o.totalPrice), 0) from WaitingTicketEntity w, OrderEntity o where w.orderId = o.id and w.status = ?1")
    long sumRevenueByStatus(WaitingStatus status);

    @Query("select coalesce(sum(o.totalPrice), 0) from WaitingTicketEntity w, OrderEntity o where w.orderId = o.id and w.businessDate = ?1 and w.status = ?2")
    long sumRevenueByDateAndStatus(LocalDate businessDate, WaitingStatus status);
}

