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
import com.investory.backend.domain.user.repository.UserRepository;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final HoldingRepository holdingRepository;

    @Transactional
    public OrderResponse.Detail createOrder(String loginId, OrderRequest.Create request) {
        User user = getUserByLoginId(loginId);
        Stock stock = getStockByCode(request.getStockCode());

        // 주문 가격 설정
        BigDecimal orderPrice = request.getOrderType() == Order.OrderType.MARKET
                ? stock.getCurrentPrice()
                : request.getPrice();

        if (orderPrice == null || orderPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_PRICE);
        }

        // 매도 주문인 경우 보유 수량 확인
        if (request.getSide() == Order.OrderSide.SELL) {
            Holding holding = holdingRepository.findByUserIdAndStockId(user.getId(), stock.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.HOLDING_NOT_FOUND));
            
            if (holding.getQuantity() < request.getQuantity()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_HOLDING);
            }
        }

        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .stock(stock)
                .orderType(request.getOrderType())
                .side(request.getSide())
                .quantity(request.getQuantity())
                .price(orderPrice)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성: {} - {} {} {} {}주 @{}", 
                loginId, stock.getCode(), request.getSide(), request.getOrderType(), 
                request.getQuantity(), orderPrice);

        // 시장가 주문은 즉시 체결 처리 (모의 거래)
        if (request.getOrderType() == Order.OrderType.MARKET) {
            executeOrder(savedOrder, orderPrice);
        }

        return OrderResponse.Detail.from(savedOrder);
    }

    @Transactional
    public void executeOrder(Order order, BigDecimal executionPrice) {
        order.fill(executionPrice, order.getQuantity());

        // 포트폴리오 업데이트
        User user = order.getUser();
        Stock stock = order.getStock();

        if (order.getSide() == Order.OrderSide.BUY) {
            // 매수: 보유 종목 추가/수량 증가
            Holding holding = holdingRepository.findByUserIdAndStockId(user.getId(), stock.getId())
                    .orElse(Holding.builder()
                            .user(user)
                            .stock(stock)
                            .quantity(0)
                            .averagePrice(BigDecimal.ZERO)
                            .totalInvestment(BigDecimal.ZERO)
                            .build());
            
            holding.addQuantity(order.getQuantity(), executionPrice);
            holdingRepository.save(holding);
        } else {
            // 매도: 보유 수량 감소
            Holding holding = holdingRepository.findByUserIdAndStockId(user.getId(), stock.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.HOLDING_NOT_FOUND));
            
            holding.reduceQuantity(order.getQuantity());
            
            if (holding.getQuantity() == 0) {
                holdingRepository.delete(holding);
            } else {
                holdingRepository.save(holding);
            }
        }

        // 경험치 추가
        user.addExperience(20);
        userRepository.save(user);

        log.info("주문 체결: {} - {} {} {}주 @{}", 
                user.getLoginId(), stock.getCode(), order.getSide(), 
                order.getQuantity(), executionPrice);
    }

    @Transactional
    public OrderResponse.Detail cancelOrder(String loginId, Long orderId, String reason) {
        User user = getUserByLoginId(loginId);
        Order order = getOrderById(orderId);

        // 권한 확인
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 취소 가능 여부 확인
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        order.cancel(reason != null ? reason : "사용자 취소");
        log.info("주문 취소: {} - 주문ID {}", loginId, orderId);

        return OrderResponse.Detail.from(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse.Summary> getOrders(String loginId, Pageable pageable) {
        User user = getUserByLoginId(loginId);
        return orderRepository.findByUserIdWithStock(user.getId(), pageable)
                .map(OrderResponse.Summary::from);
    }

    @Transactional(readOnly = true)
    public OrderResponse.Detail getOrderDetail(String loginId, Long orderId) {
        User user = getUserByLoginId(loginId);
        Order order = getOrderById(orderId);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return OrderResponse.Detail.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse.Summary> getPendingOrders(String loginId) {
        User user = getUserByLoginId(loginId);
        return orderRepository.findByUserIdAndStatus(user.getId(), Order.OrderStatus.PENDING).stream()
                .map(OrderResponse.Summary::from)
                .collect(Collectors.toList());
    }

    private User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Stock getStockByCode(String code) {
        return stockRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }
}
