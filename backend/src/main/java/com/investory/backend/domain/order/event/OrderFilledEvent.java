package com.investory.backend.domain.order.event;

import com.investory.backend.domain.order.entity.Order;

import java.math.BigDecimal;

/**
 * 주문 체결 도메인 이벤트.
 * <p>
 * <b>왜 엔티티가 아니라 값(record)을 담는가</b><br>
 * 이 이벤트는 {@code AFTER_COMMIT} 시점에 소비된다. 그 시점의 영속성 컨텍스트는 이미 닫혀 있어서
 * 엔티티를 그대로 실어 보내면 리스너에서 LazyInitializationException 이 터진다.
 * 따라서 리스너가 필요로 하는 값만 스냅샷으로 복사해 불변 record 로 전달한다.
 *
 * @param orderId     체결된 주문 ID
 * @param userId      주문자 ID
 * @param loginId     주문자 로그인 ID (리스너에서 재조회 키로 사용)
 * @param stockCode   종목 코드
 * @param stockName   종목명
 * @param side        매수/매도
 * @param quantity    체결 수량
 * @param price       체결 단가
 * @param totalAmount 체결 총액
 */
public record OrderFilledEvent(
        Long orderId,
        Long userId,
        String loginId,
        String stockCode,
        String stockName,
        Order.OrderSide side,
        int quantity,
        BigDecimal price,
        BigDecimal totalAmount
) {

    public static OrderFilledEvent from(Order order, BigDecimal executionPrice, int executedQuantity) {
        return new OrderFilledEvent(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getLoginId(),
                order.getStock().getCode(),
                order.getStock().getName(),
                order.getSide(),
                executedQuantity,
                executionPrice,
                executionPrice.multiply(BigDecimal.valueOf(executedQuantity))
        );
    }
}
