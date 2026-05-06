package com.csee.hgu.menu.order.repo;

import com.csee.hgu.menu.order.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @EntityGraph(attributePaths = {"items", "items.toppings"})
    Optional<OrderEntity> findWithItemsById(Long id);
}

