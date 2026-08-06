package com.investory.backend.domain.order.service;

import com.investory.backend.domain.order.dto.OrderRequest;
import com.investory.backend.domain.order.dto.OrderResponse;
import com.investory.backend.domain.order.entity.Order;
import com.investory.backend.domain.order.repository.OrderRepository;
import com.investory.backend.domain.portfolio.entity.Holding;
import com.investory.backend.domain.portfolio.repository.HoldingRepository;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.stock.repository.StockRepository;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 주문 접수 트랜잭션.
 * <p>
 * <b>왜 OrderService 와 분리했는가</b><br>
 * {@code @Retryable} 은 <b>트랜잭션 바깥</b>에 있어야 한다. 낙관적 락 충돌이 나면 그 트랜잭션은
 * 이미 rollback-only 로 마킹되고 영속성 컨텍스트도 오염된 상태다. 같은 트랜잭션 안에서 재시도해 봐야
 * 커밋할 수 없다. 그래서 재시도 경계({@code OrderService})와 트랜잭션 경계(이 클래스)를 다른 빈으로
 * 분리해, 재시도할 때마다 완전히 새로운 트랜잭션이 열리도록 했다.
 * <p>
 * 호출 구조: {@code OrderService(재시도)} → {@code OrderPlacer(트랜잭션)} → {@code OrderExecutor(체결)}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPlacer {

    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final HoldingRepository holdingRepository;
    private final OrderUserFinder orderUserFinder;
    private final OrderExecutor orderExecutor;

    @Transactional
    public OrderResponse.Detail place(String loginId, OrderRequest.Create request) {
        User user = orderUserFinder.findForOrder(loginId);
        Stock stock = getStockByCode(request.getStockCode());

        BigDecimal orderPrice = resolveOrderPrice(request, stock);
        validateOrderable(user, stock, request, orderPrice);

        Order order = orderRepository.save(Order.builder()
                .user(user)
                .stock(stock)
                .orderType(request.getOrderType())
                .side(request.getSide())
                .quantity(request.getQuantity())
                .price(orderPrice)
                .build());

        log.info("주문 생성: {} - {} {} {} {}주 @{} (lockMode={})",
                loginId, stock.getCode(), request.getSide(), request.getOrderType(),
                request.getQuantity(), orderPrice, orderUserFinder.strategyName());

        // 시장가는 즉시 체결. 지정가는 PENDING 으로 남아 LimitOrderMatcher 가 처리한다.
        if (request.getOrderType() == Order.OrderType.MARKET) {
            orderExecutor.execute(order, orderPrice);
        }

        return OrderResponse.Detail.from(order);
    }

    private BigDecimal resolveOrderPrice(OrderRequest.Create request, Stock stock) {
        BigDecimal orderPrice = request.getOrderType() == Order.OrderType.MARKET
                ? stock.getCurrentPrice()
                : request.getPrice();

        if (orderPrice == null || orderPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_PRICE);
        }
        return orderPrice;
    }

    /**
     * 접수 단계 사전 검증.
     * <p>
     * 여기서 잔액을 확인해도 실제 차감은 체결 시점에 일어난다. 지정가 주문은 접수와 체결 사이에
     * 시간 간격이 있어 그 사이 잔액이 줄 수 있고, 그 경우는 체결 시점에 REJECTED 로 처리된다.
     * (증거금을 미리 묶는 예약(reserve) 방식은 별도 이슈로 분리 — README 로드맵 참고)
     */
    private void validateOrderable(User user, Stock stock, OrderRequest.Create request, BigDecimal orderPrice) {
        if (request.getSide() == Order.OrderSide.BUY) {
            BigDecimal requiredCash = orderPrice.multiply(BigDecimal.valueOf(request.getQuantity()));
            if (!user.canAfford(requiredCash)) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_CASH);
            }
            return;
        }

        Holding holding = holdingRepository.findByUserIdAndStockId(user.getId(), stock.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOLDING_NOT_FOUND));

        if (holding.getQuantity() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_HOLDING);
        }
    }

    private Stock getStockByCode(String code) {
        return stockRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));
    }
}
