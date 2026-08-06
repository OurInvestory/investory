package com.investory.backend.domain.user.entity;

import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 현금 잔액 변동 이력 (Audit Log).
 * <p>
 * 설계 메모
 * <ul>
 *   <li>이 테이블은 <b>append-only</b> 다. 수정/삭제 메서드를 의도적으로 만들지 않았다.
 *       잔액 정합성 사고가 났을 때 이력을 재생(replay)해서 원인을 추적할 수 있어야 하기 때문이다.</li>
 *   <li>{@code orderId} 를 {@code Order} 연관관계가 아닌 단순 Long 으로 둔 이유:
 *       user 도메인이 order 도메인을 컴파일 타임에 의존하면 양방향 의존이 생긴다.
 *       (order → user 는 이미 존재) 식별자만 들고 있으면 도메인 경계가 유지된다.</li>
 *   <li>발생 시각은 {@link BaseEntity#getCreatedAt()} 을 그대로 사용한다.
 *       append-only 이므로 createdAt 이 곧 발생 시각이며, 별도 컬럼은 중복이다.</li>
 * </ul>
 */
@Entity
@Table(name = "cash_histories", indexes = {
    @Index(name = "idx_cash_history_user", columnList = "user_id"),
    @Index(name = "idx_cash_history_order", columnList = "order_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CashHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    /** 변동액. 부호 없는 절대값이 아니라 부호를 포함한다 (매수 = 음수, 매도/입금 = 양수). */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /** 변동 직후 잔액. 스냅샷을 남겨야 이력만으로 잔액 추이를 복원할 수 있다. */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    /** 연관 주문 ID. 입금(DEPOSIT)처럼 주문과 무관한 변동은 null. */
    @Column(name = "order_id")
    private Long orderId;

    public static CashHistory of(User user, TransactionType type,
                                 BigDecimal amount, BigDecimal balanceAfter, Long orderId) {
        return CashHistory.builder()
                .user(user)
                .type(type)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .orderId(orderId)
                .build();
    }

    public enum TransactionType {
        /** 시드머니 지급 등 외부 입금 */
        DEPOSIT,
        /** 매수 체결로 인한 출금 */
        BUY,
        /** 매도 체결로 인한 입금 */
        SELL
    }
}
