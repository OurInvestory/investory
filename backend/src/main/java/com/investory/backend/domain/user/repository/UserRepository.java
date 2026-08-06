package com.investory.backend.domain.user.repository;

import com.investory.backend.domain.user.entity.CashHistory;
import com.investory.backend.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByLoginId(String loginId);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByNickname(String nickname);
    
    Optional<User> findByProviderAndProviderId(User.AuthProvider provider, String providerId);
    
    boolean existsByLoginId(String loginId);
    
    boolean existsByEmail(String email);
    
    boolean existsByNickname(String nickname);

    /**
     * 아직 시드머니(DEPOSIT 이력)를 한 번도 받지 못한 유저 조회.
     * 백필 러너의 멱등성 판단 기준이다.
     */
    @Query("""
            SELECT u FROM User u
            WHERE NOT EXISTS (
                SELECT 1 FROM CashHistory c
                WHERE c.user = u AND c.type = :type
            )
            """)
    List<User> findUsersWithoutHistoryType(@Param("type") CashHistory.TransactionType type);

    /**
     * JPQL 안에 중첩 enum 리터럴({@code CashHistory.TransactionType.DEPOSIT})을 직접 쓰면
     * 하이버네이트 버전에 따라 파싱이 불안정하다. 파라미터 바인딩으로 넘기고,
     * 호출부 가독성은 default 메서드로 확보한다.
     */
    default List<User> findUsersWithoutDepositHistory() {
        return findUsersWithoutHistoryType(CashHistory.TransactionType.DEPOSIT);
    }

    /**
     * 비관적 쓰기 락(SELECT ... FOR UPDATE)으로 유저를 조회한다.
     * <p>
     * 낙관적 락 + 재시도 방식과의 성능 비교 실험용이며,
     * {@code investory.order.lock-mode=pessimistic} 일 때만 사용된다.
     * 락 획득 대기를 무한정 기다리지 않도록 타임아웃을 3초로 건다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("SELECT u FROM User u WHERE u.loginId = :loginId")
    Optional<User> findByLoginIdForUpdate(@Param("loginId") String loginId);
}
