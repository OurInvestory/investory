package com.investory.backend.domain.user.repository;

import com.investory.backend.domain.user.entity.CashHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CashHistoryRepository extends JpaRepository<CashHistory, Long> {

    Page<CashHistory> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    List<CashHistory> findByUserIdOrderByIdAsc(Long userId);

    List<CashHistory> findByOrderId(Long orderId);

    /**
     * 유저의 전체 변동액 합계.
     * 동시성 테스트에서 "이력 합계 == 실제 잔액 변화" 정합성을 검증하는 데 사용한다.
     * 이력이 하나도 없으면 null 이 아니라 0 이 나오도록 COALESCE 로 감싼다.
     */
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CashHistory c WHERE c.user.id = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
