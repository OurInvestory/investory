package com.investory.backend.global.sse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 서버 전송 이벤트(SSE) 엔드포인트.
 * <p>
 * <b>인증 관련 주의</b><br>
 * 브라우저 기본 {@code EventSource} 는 커스텀 헤더를 못 붙인다. 즉 Authorization 헤더로
 * 인증하는 이 프로젝트에서는 {@code /orders/stream} 을 EventSource 로 직접 호출할 수 없다.
 * 프론트엔드는 {@code @microsoft/fetch-event-source} 같은 fetch 기반 폴리필을 써야 한다.
 * 토큰을 쿼리 파라미터로 받는 방법도 있지만, 접근 로그와 Referer 헤더에 토큰이 그대로 남아
 * 유출 경로가 늘어나므로 택하지 않았다.
 */
@Tag(name = "실시간 스트림", description = "SSE 기반 실시간 이벤트 API")
@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @Operation(summary = "시세 스트림 구독",
            description = "시뮬레이터가 시세를 갱신할 때마다 전 종목 스냅샷을 push 합니다. 인증 불필요.")
    @GetMapping(value = "/stocks/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStockPrices() {
        return sseService.subscribeStockPrice();
    }

    @Operation(summary = "내 주문 체결 알림 구독",
            description = "본인 주문이 체결될 때마다 체결 내역을 push 합니다. 인증 필요.")
    @GetMapping(value = "/orders/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOrderFills(@AuthenticationPrincipal UserDetails userDetails) {
        return sseService.subscribeOrderFilled(userDetails.getUsername());
    }
}
