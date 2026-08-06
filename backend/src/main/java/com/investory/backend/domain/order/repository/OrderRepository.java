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

    /**
     * 특정 종목의 미체결 지정가 주문을 ID 오름차순으로 조회한다. (지정가 매칭용)
     * <p>
     * <b>왜 offset 페이징이 아니라 keyset 페이징인가</b><br>
     * 매칭이 진행되면 체결된 주문은 PENDING 에서 빠져나간다. offset 방식이면 그만큼
     * 뒤 페이지가 앞으로 당겨져 <b>주문을 건너뛰는</b> 버그가 생긴다.
     * 마지막으로 본 ID 기준으로 잘라 읽으면 목록이 줄어도 누락이 없다.
     * <p>
     * 체결 순서는 ID 오름차순 = 접수 순서(FIFO)다. 실제 거래소의 가격-시간 우선 원칙 중
     * 시간 우선만 구현한 형태이며, 모의 거래에서는 이 정도로 충분하다.
     */
    @Query("""
            SELECT o FROM Order o
            WHERE o.stock.id = :stockId
              AND o.status = :status
              AND o.orderType = :orderType
              AND o.id > :lastSeenId
            ORDER BY o.id ASC
            """)
    List<Order> findMatchableOrders(@Param("stockId") Long stockId,
                                    @Param("status") Order.OrderStatus status,
                                    @Param("orderType") Order.OrderType orderType,
                                    @Param("lastSeenId") Long lastSeenId,
                                    Pageable pageable);
}
