package com.investory.backend.global.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSE 연결(Emitter) 인메모리 저장소.
 * <p>
 * <b>인메모리의 한계를 먼저 적어 둔다</b><br>
 * 이 구현은 <b>단일 인스턴스에서만</b> 동작한다. 서버를 2대 이상으로 스케일 아웃하면
 * A 서버에 붙은 클라이언트는 B 서버에서 발생한 체결 알림을 받지 못한다.
 * 해결하려면 Redis Pub/Sub 으로 인스턴스 간 이벤트를 브로드캐스트해야 한다
 * (이 프로젝트는 이미 Redis 를 쓰므로 전환 비용이 크지 않다 — README 로드맵 참고).
 * 무료 티어 단일 인스턴스 배포가 전제라 지금은 인메모리로 충분하다.
 * <p>
 * <b>자료구조 선택</b><br>
 * 한 사용자가 여러 탭을 열 수 있으므로 키당 여러 Emitter 를 담는다.
 * 읽기(브로드캐스트)가 쓰기(연결/해제)보다 압도적으로 잦아 {@link CopyOnWriteArrayList} 가 적합하다.
 */
@Slf4j
@Repository
public class SseEmitterRepository {

    /** 채널 키 → 연결 목록. 키는 "stock" 같은 공용 채널이거나 "user:{loginId}" 형태다. */
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(String key, SseEmitter emitter) {
        emitters.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // 정상 종료 / 타임아웃 / 에러 모두에서 반드시 제거해야 한다.
        // 누락되면 죽은 Emitter 가 계속 쌓여 메모리 누수가 된다.
        emitter.onCompletion(() -> remove(key, emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            remove(key, emitter);
        });
        emitter.onError(e -> {
            emitter.complete();
            remove(key, emitter);
        });

        return emitter;
    }

    private void remove(String key, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(key);
        if (list == null) {
            return;
        }
        list.remove(emitter);
        // 빈 리스트를 남겨 두면 키가 무한히 늘어난다. 비면 키까지 정리한다.
        // remove(key, value) 2-인자 버전을 쓰는 이유: 같은 순간 다른 스레드가
        // 이 키에 새 Emitter 를 넣었다면 그 리스트를 통째로 날려선 안 되기 때문이다.
        if (list.isEmpty()) {
            emitters.remove(key, list);
        }
    }

    /**
     * 해당 키에 연결된 모든 클라이언트에 이벤트를 보낸다.
     * 전송에 실패한 연결은 이미 끊긴 것으로 보고 정리한다.
     */
    public void send(String key, String eventName, Object payload) {
        List<SseEmitter> list = emitters.get(key);
        if (list == null || list.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(payload));
            } catch (IOException | IllegalStateException e) {
                // 클라이언트가 탭을 닫으면 여기서 IOException 이 난다. 정상 상황이므로 debug 레벨.
                log.debug("SSE 전송 실패 - 연결 정리: key={}", key);
                emitter.complete();
                remove(key, emitter);
            }
        }
    }

    /** 모니터링용: 현재 유지 중인 연결 수. */
    public int connectionCount() {
        return emitters.values().stream().mapToInt(List::size).sum();
    }
}
