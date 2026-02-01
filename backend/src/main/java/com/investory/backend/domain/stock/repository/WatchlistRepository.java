package com.investory.backend.domain.stock.repository;

import com.investory.backend.domain.stock.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    
    List<Watchlist> findByUserIdOrderBySortOrderAsc(Long userId);
    
    List<Watchlist> findByUserIdAndGroupNameOrderBySortOrderAsc(Long userId, String groupName);
    
    Optional<Watchlist> findByUserIdAndStockId(Long userId, Long stockId);
    
    boolean existsByUserIdAndStockId(Long userId, Long stockId);
    
    @Query("SELECT DISTINCT w.groupName FROM Watchlist w WHERE w.user.id = :userId")
    List<String> findGroupNamesByUserId(@Param("userId") Long userId);
    
    void deleteByUserIdAndStockId(Long userId, Long stockId);
    
    long countByUserId(Long userId);
}
