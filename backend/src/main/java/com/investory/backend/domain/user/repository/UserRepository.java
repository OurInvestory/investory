package com.investory.backend.domain.user.repository;

import com.investory.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
