package com.investory.backend.domain.reward.listener;

import com.investory.backend.domain.order.event.OrderFilledEvent;
import com.investory.backend.domain.reward.service.ExperienceGranter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 주문 체결 → 경험치 적립 연결 지점.
 * <p>
 * 기존에는 {@code OrderService.executeOrder} 안에서 {@code user.addExperience(20)} 을 직접 호출해
 * 주문 도메인이 게이미피케이션 정책을 알고 있었다. 이벤트로 끊어서, 주문 도메인은
 * "체결됐다"는 사실만 발행하고 그걸로 무엇을 할지는 reward 도메인이 결정한다.
 * 나중에 업적 해금, 알림 발송이 추가돼도 주문 코드는 바뀌지 않는다.
 * <p>
 * <b>왜 AFTER_COMMIT 인가</b><br>
 * 체결 트랜잭션이 롤백되면 경험치도 없던 일이 되어야 한다. 커밋 확정 후에만 반응하도록
 * {@code AFTER_COMMIT} 을 쓴다. 일반 {@code @EventListener} 나 {@code BEFORE_COMMIT} 이면
 * 롤백된 주문에 대해서도 경험치가 올라갈 수 있다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFilledEventListener {

    /** 주문 체결 1건당 지급 경험치. */
    private static final int EXP_PER_FILLED_ORDER = 20;

    private final ExperienceGranter experienceGranter;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderFilled(OrderFilledEvent event) {
        try {
            experienceGranter.grant(event.userId(), EXP_PER_FILLED_ORDER);
            log.debug("체결 경험치 적립: userId={}, orderId={}, exp=+{}",
                    event.userId(), event.orderId(), EXP_PER_FILLED_ORDER);
        } catch (Exception e) {
            // 부가 기능 실패가 이미 커밋된 체결을 되돌릴 수는 없다.
            // AFTER_COMMIT 리스너에서 예외를 던지면 호출 스택 위쪽으로 전파되어
            // 정상 처리된 주문 응답까지 실패로 만들 수 있으므로 반드시 여기서 막는다.
            log.error("체결 후처리 실패: orderId={}", event.orderId(), e);
        }
    }
}
