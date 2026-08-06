package com.investory.backend.domain.user.service;

import com.investory.backend.domain.user.entity.CashHistory;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.CashHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 현금 잔액 변동을 담당하는 도메인 서비스.
 * <p>
 * <b>왜 별도 서비스로 뺐는가</b><br>
 * "잔액 변경"과 "이력 기록"은 항상 한 쌍으로 일어나야 한다. 이 규칙을 OrderExecutor,
 * AuthService, 백필 러너에 각각 흩어 두면 언젠가 한 곳에서 이력 기록이 빠진다.
 * 잔액을 만지는 유일한 통로를 여기로 강제해서 그 사고를 구조적으로 막는다.
 * <p>
 * 모든 메서드는 {@code MANDATORY} 가 아닌 기본 전파 속성({@code REQUIRED})을 쓴다.
 * 호출자(주문 체결 트랜잭션)의 트랜잭션에 참여해서, 체결이 롤백되면 잔액 변동과 이력도
 * 함께 롤백되어야 하기 때문이다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CashService {

    private final CashHistoryRepository cashHistoryRepository;

    /**
     * 매수 체결에 따른 출금.
     * 잔액 부족이면 {@link User#withdraw} 가 INSUFFICIENT_CASH 예외를 던진다.
     */
    @Transactional
    public void withdrawForBuy(User user, BigDecimal amount, Long orderId) {
        BigDecimal balanceAfter = user.withdraw(amount);
        // 매수는 잔액이 줄어드는 방향이므로 이력에는 음수로 남긴다.
        record(user, CashHistory.TransactionType.BUY, amount.negate(), balanceAfter, orderId);
        log.debug("매수 출금: userId={}, amount={}, balanceAfter={}", user.getId(), amount, balanceAfter);
    }

    /** 매도 체결에 따른 입금. */
    @Transactional
    public void depositForSell(User user, BigDecimal amount, Long orderId) {
        BigDecimal balanceAfter = user.deposit(amount);
        record(user, CashHistory.TransactionType.SELL, amount, balanceAfter, orderId);
        log.debug("매도 입금: userId={}, amount={}, balanceAfter={}", user.getId(), amount, balanceAfter);
    }

    /** 시드머니 등 주문과 무관한 입금. */
    @Transactional
    public void deposit(User user, BigDecimal amount) {
        BigDecimal balanceAfter = user.deposit(amount);
        record(user, CashHistory.TransactionType.DEPOSIT, amount, balanceAfter, null);
        log.info("현금 입금: userId={}, amount={}, balanceAfter={}", user.getId(), amount, balanceAfter);
    }

    private void record(User user, CashHistory.TransactionType type,
                        BigDecimal signedAmount, BigDecimal balanceAfter, Long orderId) {
        cashHistoryRepository.save(CashHistory.of(user, type, signedAmount, balanceAfter, orderId));
    }
}
