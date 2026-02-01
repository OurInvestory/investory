package com.investory.backend.domain.stock.repository;

import com.investory.backend.domain.stock.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    Optional<Stock> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Stock> findByMarket(Stock.Market market);
    
    @Query("SELECT s FROM Stock s WHERE s.name LIKE %:keyword% OR s.code LIKE %:keyword% OR s.englishName LIKE %:keyword%")
    Page<Stock> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT s FROM Stock s WHERE s.market = :market ORDER BY s.volume DESC")
    List<Stock> findTopByMarketOrderByVolume(@Param("market") Stock.Market market, Pageable pageable);
    
    @Query("SELECT s FROM Stock s ORDER BY s.volume DESC")
    List<Stock> findTopByVolume(Pageable pageable);
    
    @Query("SELECT s FROM Stock s WHERE s.changeRate > 0 ORDER BY s.changeRate DESC")
    List<Stock> findTopGainers(Pageable pageable);
    
    @Query("SELECT s FROM Stock s WHERE s.changeRate < 0 ORDER BY s.changeRate ASC")
    List<Stock> findTopLosers(Pageable pageable);
    
    List<Stock> findBySector(String sector);
    
    @Query("SELECT DISTINCT s.sector FROM Stock s WHERE s.sector IS NOT NULL")
    List<String> findAllSectors();
}
