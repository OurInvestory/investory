package com.investory.backend.domain.stock.service;

import com.investory.backend.domain.stock.dto.StockResponse;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.stock.entity.Watchlist;
import com.investory.backend.domain.stock.repository.StockRepository;
import com.investory.backend.domain.stock.repository.WatchlistRepository;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<StockResponse.Summary> getAllStocks(Stock.Market market) {
        List<Stock> stocks = market != null 
                ? stockRepository.findByMarket(market)
                : stockRepository.findAll();
        return stocks.stream()
                .map(StockResponse.Summary::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StockResponse.Detail getStockDetail(String code) {
        Stock stock = getStockByCode(code);
        return StockResponse.Detail.from(stock);
    }

    @Transactional(readOnly = true)
    public Page<StockResponse.Summary> searchStocks(String keyword, Pageable pageable) {
        return stockRepository.searchByKeyword(keyword, pageable)
                .map(StockResponse.Summary::from);
    }

    @Transactional(readOnly = true)
    public List<StockResponse.Summary> getTopStocks(String type, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Stock> stocks = switch (type.toLowerCase()) {
            case "volume" -> stockRepository.findTopByVolume(pageable);
            case "gainers" -> stockRepository.findTopGainers(pageable);
            case "losers" -> stockRepository.findTopLosers(pageable);
            default -> stockRepository.findTopByVolume(pageable);
        };
        return stocks.stream()
                .map(StockResponse.Summary::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getAllSectors() {
        return stockRepository.findAllSectors();
    }

    @Transactional(readOnly = true)
    public List<StockResponse.Summary> getStocksBySector(String sector) {
        return stockRepository.findBySector(sector).stream()
                .map(StockResponse.Summary::from)
                .collect(Collectors.toList());
    }

    // 호가 조회 (모의 데이터)
    @Transactional(readOnly = true)
    public StockResponse.Orderbook getOrderbook(String code) {
        Stock stock = getStockByCode(code);
        BigDecimal basePrice = stock.getCurrentPrice();

        List<StockResponse.OrderbookEntry> asks = new ArrayList<>();
        List<StockResponse.OrderbookEntry> bids = new ArrayList<>();

        // 매도 호가 (현재가 위로 5단계)
        for (int i = 5; i >= 1; i--) {
            BigDecimal price = basePrice.add(basePrice.multiply(BigDecimal.valueOf(0.01 * i)));
            asks.add(StockResponse.OrderbookEntry.builder()
                    .price(price.setScale(0, java.math.RoundingMode.HALF_UP))
                    .quantity((long) (Math.random() * 10000) + 100)
                    .count((int) (Math.random() * 50) + 1)
                    .build());
        }

        // 매수 호가 (현재가 아래로 5단계)
        for (int i = 1; i <= 5; i++) {
            BigDecimal price = basePrice.subtract(basePrice.multiply(BigDecimal.valueOf(0.01 * i)));
            bids.add(StockResponse.OrderbookEntry.builder()
                    .price(price.setScale(0, java.math.RoundingMode.HALF_UP))
                    .quantity((long) (Math.random() * 10000) + 100)
                    .count((int) (Math.random() * 50) + 1)
                    .build());
        }

        return StockResponse.Orderbook.builder()
                .stockCode(code)
                .asks(asks)
                .bids(bids)
                .build();
    }

    // 관심 종목
    @Transactional(readOnly = true)
    public List<StockResponse.WatchlistItem> getWatchlist(String loginId) {
        User user = getUserByLoginId(loginId);
        return watchlistRepository.findByUserIdOrderBySortOrderAsc(user.getId()).stream()
                .map(StockResponse.WatchlistItem::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockResponse.WatchlistItem> getWatchlistByGroup(String loginId, String groupName) {
        User user = getUserByLoginId(loginId);
        return watchlistRepository.findByUserIdAndGroupNameOrderBySortOrderAsc(user.getId(), groupName).stream()
                .map(StockResponse.WatchlistItem::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getWatchlistGroups(String loginId) {
        User user = getUserByLoginId(loginId);
        return watchlistRepository.findGroupNamesByUserId(user.getId());
    }

    @Transactional
    public StockResponse.WatchlistItem addToWatchlist(String loginId, String stockCode, String groupName) {
        User user = getUserByLoginId(loginId);
        Stock stock = getStockByCode(stockCode);

        if (watchlistRepository.existsByUserIdAndStockId(user.getId(), stock.getId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_WATCHLIST);
        }

        long count = watchlistRepository.countByUserId(user.getId());

        Watchlist watchlist = Watchlist.builder()
                .user(user)
                .stock(stock)
                .groupName(groupName != null ? groupName : "기본")
                .sortOrder((int) count)
                .build();

        Watchlist saved = watchlistRepository.save(watchlist);
        log.info("관심종목 추가: {} - {}", loginId, stockCode);

        return StockResponse.WatchlistItem.from(saved);
    }

    @Transactional
    public void removeFromWatchlist(String loginId, String stockCode) {
        User user = getUserByLoginId(loginId);
        Stock stock = getStockByCode(stockCode);

        watchlistRepository.deleteByUserIdAndStockId(user.getId(), stock.getId());
        log.info("관심종목 삭제: {} - {}", loginId, stockCode);
    }

    @Transactional(readOnly = true)
    public boolean isInWatchlist(String loginId, String stockCode) {
        User user = getUserByLoginId(loginId);
        Stock stock = getStockByCode(stockCode);
        return watchlistRepository.existsByUserIdAndStockId(user.getId(), stock.getId());
    }

    private Stock getStockByCode(String code) {
        return stockRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));
    }

    private User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
