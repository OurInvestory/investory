package com.investory.backend.domain.order.repository;

import com.investory.backend.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o JOIN FETCH o.stock WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    Page<Order> findByUserIdWithStock(@Param("userId") Long userId, Pageable pageable);
    
    List<Order> findByUserIdAndStatusIn(Long userId, List<Order.OrderStatus> statuses);
    
    long countByUserIdAndStatus(Long userId, Order.OrderStatus status);
}
