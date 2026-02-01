package com.investory.backend.domain.portfolio.repository;

import com.investory.backend.domain.portfolio.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
    
    List<Holding> findByUserId(Long userId);
    
    Optional<Holding> findByUserIdAndStockId(Long userId, Long stockId);
    
    boolean existsByUserIdAndStockId(Long userId, Long stockId);
    
    @Query("SELECT h FROM Holding h JOIN FETCH h.stock WHERE h.user.id = :userId")
    List<Holding> findByUserIdWithStock(@Param("userId") Long userId);
    
    @Query("SELECT h FROM Holding h JOIN FETCH h.stock WHERE h.user.id = :userId AND h.stock.market IN :markets")
    List<Holding> findByUserIdAndMarkets(@Param("userId") Long userId, @Param("markets") List<com.investory.backend.domain.stock.entity.Stock.Market> markets);
    
    void deleteByUserIdAndStockId(Long userId, Long stockId);
    
    long countByUserId(Long userId);
}
