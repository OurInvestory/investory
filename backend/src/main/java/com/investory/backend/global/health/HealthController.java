package com.investory.backend.global.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * 외부 핑 서비스(cron-job.org, UptimeRobot 등)가 호출하는 경량 헬스체크.
 * <p>
 * <b>왜 actuator/health 를 그냥 쓰지 않는가</b><br>
 * {@code /actuator/health} 는 DataSource, Redis, Mail 등 등록된 모든 HealthIndicator 를 실행한다.
 * 10분마다 무료 티어 DB에 커넥션을 열고 검증 쿼리를 날리는 셈이라,
 * <ul>
 *   <li>Aiven 무료 플랜(1 vCPU)의 커넥션 예산을 아무 이유 없이 갉아먹고</li>
 *   <li>DB 가 잠깐 불안정하면 503 이 나가 핑 서비스가 "서버 다운"으로 오탐한다.
 *       Keep-alive 의 목적은 <b>프로세스를 깨워 두는 것</b>이지 의존성 상태 판정이 아니다.</li>
 * </ul>
 * 그래서 어떤 I/O 도 하지 않고 상수만 돌려주는 엔드포인트를 따로 둔다.
 * 의존성까지 포함한 진짜 상태 점검이 필요하면 {@code /api/actuator/health} 를 쓰면 된다.
 * <p>
 * 컨텍스트 패스가 {@code /api} 이므로 실제 경로는 <b>{@code /api/health}</b> 다.
 */
@Tag(name = "헬스체크", description = "서버 생존 확인 API")
@RestController
public class HealthController {

    @Operation(summary = "생존 확인",
            description = "의존성을 검사하지 않고 프로세스 생존만 알립니다. 외부 keep-alive 핑 용도입니다.")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString()
        ));
    }
}
