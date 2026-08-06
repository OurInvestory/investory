package com.investory.backend.support;

import com.investory.backend.domain.order.repository.OrderRepository;
import com.investory.backend.domain.portfolio.repository.HoldingRepository;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.stock.repository.StockRepository;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.CashHistoryRepository;
import com.investory.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

/**
 * 통합 테스트 공통 베이스.
 * <p>
 * <b>클래스 레벨에 @Transactional 을 붙이지 않은 이유</b><br>
 * 테스트 메서드를 트랜잭션으로 감싸면 편하지만, 동시성 테스트에서는 치명적이다.
 * 별도 스레드는 테스트 트랜잭션에 참여하지 않으므로 테스트가 만든 픽스처를 아예 보지 못하고,
 * 낙관적 락 충돌도 재현되지 않는다. 그래서 롤백에 기대지 않고 {@link #cleanUp()} 으로 직접 지운다.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected StockRepository stockRepository;
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected HoldingRepository holdingRepository;
    @Autowired
    protected CashHistoryRepository cashHistoryRepository;

    /**
     * FK 제약 때문에 삭제 순서가 중요하다.
     * 자식(이력/주문/보유) → 부모(유저/종목) 순으로 지운다.
     */
    @AfterEach
    void cleanUp() {
        cashHistoryRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        holdingRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
    }

    protected User createUser(String loginId, BigDecimal cash) {
        return userRepository.saveAndFlush(User.builder()
                .loginId(loginId)
                .password("{noop}password")
                .email(loginId + "@investory.test")
                .nickname(loginId)
                .cash(cash)
                .build());
    }

    protected Stock createStock(String code, String name, BigDecimal price) {
        return stockRepository.saveAndFlush(Stock.builder()
                .code(code)
                .name(name)
                .market(Stock.Market.KOSPI)
                .sector("테스트")
                .currentPrice(price)
                .previousClose(price)
                .build());
    }

    /** 테스트 가독성을 위한 헬퍼. {@code won(10_000)} 형태로 쓴다. */
    protected static BigDecimal won(long amount) {
        return BigDecimal.valueOf(amount).setScale(2);
    }
}
