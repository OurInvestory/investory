package com.investory.backend.domain.order.service;

import com.investory.backend.domain.user.entity.User;

/**
 * 주문 처리 시 주문자를 조회하는 전략.
 * <p>
 * 낙관적 락(+재시도) 방식과 비관적 락 방식을 같은 테스트로 비교하기 위해 조회 지점을 추상화했다.
 * 프로파일이나 브랜치를 나누는 대신 인터페이스로 뺀 이유는, 두 구현이 <b>동시에 컴파일되고</b>
 * {@code investory.order.lock-mode} 프로퍼티 하나로 전환되어야 실험 재현이 쉽기 때문이다.
 */
public interface OrderUserFinder {

    /**
     * 주문자를 조회한다. 구현에 따라 조회 시점에 DB 락을 잡을 수도 있다.
     *
     * @throws com.investory.backend.global.exception.BusinessException 유저가 없으면 USER_NOT_FOUND
     */
    User findForOrder(String loginId);

    /** 로그 및 실험 리포트에 찍을 전략 이름. */
    String strategyName();
}
