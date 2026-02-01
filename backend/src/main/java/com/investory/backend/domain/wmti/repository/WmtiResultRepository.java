package com.investory.backend.domain.wmti.repository;

import com.investory.backend.domain.wmti.entity.WmtiResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WmtiResultRepository extends JpaRepository<WmtiResult, Long> {
    
    List<WmtiResult> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<WmtiResult> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    
    long countByUserId(Long userId);
}
