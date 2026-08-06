package com.investory.backend.domain.order.service;

import com.investory.backend.domain.order.entity.Order;
import com.investory.backend.domain.order.event.OrderFilledEvent;
import com.investory.backend.domain.order.repository.OrderRepository;
import com.investory.backend.domain.portfolio.entity.Holding;
import com.investory.backend.domain.portfolio.repository.HoldingRepository;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.service.CashService;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 주문 체결 처리 전담 컴포넌트.
 * <p>
 * <b>왜 OrderService 에서 분리했는가</b><br>
 * 기존 코드는 {@code OrderService.createOrder} 가 같은 클래스의 {@code @Transactional executeOrder}
 * 를 직접 호출했다. 스프링 AOP 는 프록시 기반이라 이 내부 호출은 프록시를 타지 않고,
 * {@code executeOrder} 에 붙은 트랜잭션 애노테이션은 아무 효과가 없었다.
 * (지금은 호출자가 이미 트랜잭션 안이라 동작에 문제가 없었지만, 애노테이션이 거짓말을 하고 있었다.)
 * 별도 빈으로 빼면 호출이 반드시 프록시를 경유하므로 트랜잭션 경계가 코드에 적힌 그대로 동작한다.
 * <p>
 * <b>전파 속성</b><br>
 * 두 진입점 모두 {@code REQUIRED} 다.
 * <ul>
 *   <li>시장가 주문: {@link OrderPlacer} 트랜잭션에 참여 → 주문 저장과 체결이 원자적으로 묶인다.</li>
 *   <li>지정가 매칭: {@link LimitOrderMatcher} 가 트랜잭션 없이 호출 → 주문 1건당 트랜잭션이 열린다.
 *       한 건이 실패해도 나머지 체결은 롤백되지 않는다.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExecutor {

    private final OrderRepository orderRepository;
    private final HoldingRepository holdingRepository;
    private final CashService cashService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 영속 상태의 주문을 체결한다. (시장가 경로)
     * 호출자의 트랜잭션에 참여한다.
     */
    @Transactional
    public void execute(Order order, BigDecimal executionPrice) {
        doExecute(order, executionPrice);
    }

    /**
     * 주문 ID로 재조회 후 체결한다. (지정가 매칭 경로)
     * <p>
     * 엔티티가 아닌 ID를 받는 이유: 매처는 트랜잭션 밖에서 대상 목록을 뽑기 때문에
     * 그때 읽은 엔티티는 준영속(detached) 상태다. 트랜잭션 안에서 다시 읽어야
     * 더티 체킹과 {@code @Version} 검사가 정상 동작한다.
     *
     * @return 체결 성공 여부 (잔액 부족 등으로 거부되면 false)
     */
    @Transactional
    public boolean executeById(Long orderId, BigDecimal executionPrice) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 매처가 목록을 뽑은 뒤 체결까지의 사이에 사용자가 취소했을 수 있다. 반드시 재확인한다.
        if (!order.isPending()) {
            log.debug("체결 스킵 - 이미 처리된 주문: orderId={}, status={}", orderId, order.getStatus());
            return false;
        }

        try {
            doExecute(order, executionPrice);
            return true;
        } catch (BusinessException e) {
            // 잔액/보유 부족으로 체결 불가한 지정가 주문은 계속 대기시키지 않고 REJECTED 로 종료한다.
            // (대안: PENDING 유지 후 재시도 → 영원히 체결 안 되는 좀비 주문이 쌓인다)
            if (isRejectable(e.getErrorCode())) {
                order.reject(e.getErrorCode().getMessage());
                log.info("지정가 주문 거부: orderId={}, reason={}", orderId, e.getErrorCode().getMessage());
                return false;
            }
            throw e;
        }
    }

    private boolean isRejectable(ErrorCode errorCode) {
        return errorCode == ErrorCode.INSUFFICIENT_CASH
                || errorCode == ErrorCode.INSUFFICIENT_HOLDING
                || errorCode == ErrorCode.HOLDING_NOT_FOUND;
    }

    private void doExecute(Order order, BigDecimal executionPrice) {
        User user = order.getUser();
        Stock stock = order.getStock();
        int quantity = order.getQuantity();
        BigDecimal totalAmount = executionPrice.multiply(BigDecimal.valueOf(quantity));

        if (order.getSide() == Order.OrderSide.BUY) {
            executeBuy(order, user, stock, quantity, executionPrice, totalAmount);
        } else {
            executeSell(order, user, stock, quantity, totalAmount);
        }

        order.fill(executionPrice, quantity);

        // 경험치 적립 같은 부가 관심사는 여기서 직접 호출하지 않고 이벤트로 넘긴다.
        // (기존 user.addExperience(20) 직접 호출 제거)
        eventPublisher.publishEvent(OrderFilledEvent.from(order, executionPrice, quantity));

        log.info("주문 체결: {} - {} {} {}주 @{}",
                user.getLoginId(), stock.getCode(), order.getSide(), quantity, executionPrice);
    }

    private void executeBuy(Order order, User user, Stock stock, int quantity,
                            BigDecimal executionPrice, BigDecimal totalAmount) {
        // 1) 현금 차감이 먼저다. 잔액이 모자라면 여기서 예외가 나고 보유 수량은 손대지 않는다.
        cashService.withdrawForBuy(user, totalAmount, order.getId());

        // 2) 보유 종목 신규 생성 또는 수량 증가
        Holding holding = holdingRepository.findByUserIdAndStockId(user.getId(), stock.getId())
                .orElseGet(() -> Holding.builder()
                        .user(user)
                        .stock(stock)
                        .quantity(0)
                        .averagePrice(BigDecimal.ZERO)
                        .totalInvestment(BigDecimal.ZERO)
                        .build());

        holding.addQuantity(quantity, executionPrice);
        holdingRepository.save(holding);
    }

    private void executeSell(Order order, User user, Stock stock, int quantity, BigDecimal totalAmount) {
        Holding holding = holdingRepository.findByUserIdAndStockId(user.getId(), stock.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOLDING_NOT_FOUND));

        if (holding.getQuantity() < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_HOLDING);
        }

        holding.reduceQuantity(quantity);
        if (holding.getQuantity() == 0) {
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }

        cashService.depositForSell(user, totalAmount, order.getId());
    }
}
