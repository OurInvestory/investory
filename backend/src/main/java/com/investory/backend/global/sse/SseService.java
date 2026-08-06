package com.investory.backend.global.sse;

import com.investory.backend.domain.order.event.OrderFilledEvent;
import com.investory.backend.domain.stock.event.StockPriceUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 구독 및 이벤트 중계.
 * <p>
 * 도메인 이벤트({@link StockPriceUpdatedEvent}, {@link OrderFilledEvent})를 구독해
 * 연결된 클라이언트에 전달한다. 도메인 서비스가 SSE 를 직접 알지 못하게 하려는 배치다.
 * 나중에 전송 수단을 WebSocket 이나 FCM 으로 바꿔도 도메인 코드는 그대로다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    /** 시세 브로드캐스트 채널 (전체 공용) */
    private static final String CHANNEL_STOCK_PRICE = "stock-price";
    /** 사용자별 체결 알림 채널 접두사 */
    private static final String CHANNEL_USER_PREFIX = "user:";

    /**
     * SSE 타임아웃(ms).
     * 무한(0)으로 두면 죽은 연결을 서버가 영영 못 놓아준다. 30분으로 끊고
     * 클라이언트(EventSource)가 자동 재연결하도록 맡긴다.
     */
    private static final long TIMEOUT_MS = 30L * 60 * 1000;

    private final SseEmitterRepository emitterRepository;

    /** 시세 스트림 구독 (인증 불필요) */
    public SseEmitter subscribeStockPrice() {
        return subscribe(CHANNEL_STOCK_PRICE);
    }

    /** 내 주문 체결 알림 구독 (인증 필요) */
    public SseEmitter subscribeOrderFilled(String loginId) {
        return subscribe(CHANNEL_USER_PREFIX + loginId);
    }

    private SseEmitter subscribe(String key) {
        SseEmitter emitter = emitterRepository.add(key, new SseEmitter(TIMEOUT_MS));

        // 최초 더미 이벤트를 즉시 보낸다.
        // 이걸 안 보내면 첫 데이터가 나갈 때까지 프록시(nginx 등)가 응답을 버퍼링해서
        // 클라이언트가 연결 성공을 인지하지 못하고 타임아웃으로 끊는 일이 생긴다.
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    /**
     * 시세 갱신 중계.
     * 시세 트랜잭션이 커밋된 뒤에만 내보낸다. 롤백된 시세를 클라이언트가 먼저 보면
     * 화면과 DB 가 어긋난다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPriceUpdated(StockPriceUpdatedEvent event) {
        emitterRepository.send(CHANNEL_STOCK_PRICE, "price-update", event.ticks());
    }

    /** 체결 알림 중계. 주문자 본인 채널로만 보낸다. */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderFilled(OrderFilledEvent event) {
        emitterRepository.send(CHANNEL_USER_PREFIX + event.loginId(), "order-filled", event);
    }
}
