package com.investory.backend.domain.portfolio.service;

import com.investory.backend.domain.portfolio.dto.PortfolioResponse;
import com.investory.backend.domain.portfolio.entity.Holding;
import com.investory.backend.domain.portfolio.repository.HoldingRepository;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PortfolioResponse.Summary getPortfolio(String loginId) {
        User user = getUserByLoginId(loginId);
        List<Holding> holdings = holdingRepository.findByUserIdWithStock(user.getId());

        // 총 평가금액 계산
        BigDecimal totalValue = holdings.stream()
                .map(h -> h.getCurrentValue(h.getStock().getCurrentPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 총 투자금액
        BigDecimal totalInvestment = holdings.stream()
                .map(Holding::getTotalInvestment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 총 수익금
        BigDecimal totalProfitLoss = totalValue.subtract(totalInvestment);

        // 총 수익률
        BigDecimal totalProfitLossRate = totalInvestment.compareTo(BigDecimal.ZERO) > 0
                ? totalProfitLoss.divide(totalInvestment, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // 일일 수익금 (전일 대비)
        BigDecimal dailyProfitLoss = holdings.stream()
                .map(h -> {
                    Stock stock = h.getStock();
                    BigDecimal priceChange = stock.getCurrentPrice().subtract(stock.getPreviousClose());
                    return priceChange.multiply(BigDecimal.valueOf(h.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 일일 수익률
        BigDecimal previousTotalValue = totalValue.subtract(dailyProfitLoss);
        BigDecimal dailyProfitLossRate = previousTotalValue.compareTo(BigDecimal.ZERO) > 0
                ? dailyProfitLoss.divide(previousTotalValue, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // 보유 종목 리스트
        List<PortfolioResponse.HoldingItem> holdingItems = holdings.stream()
                .map(h -> PortfolioResponse.HoldingItem.from(h, totalValue))
                .collect(Collectors.toList());

        return PortfolioResponse.Summary.builder()
                .totalValue(totalValue)
                .totalInvestment(totalInvestment)
                .totalProfitLoss(totalProfitLoss)
                .totalProfitLossRate(totalProfitLossRate)
                .dailyProfitLoss(dailyProfitLoss)
                .dailyProfitLossRate(dailyProfitLossRate)
                .holdingCount(holdings.size())
                .holdings(holdingItems)
                .build();
    }

    @Transactional(readOnly = true)
    public PortfolioResponse.Summary getPortfolioByMarket(String loginId, String marketType) {
        User user = getUserByLoginId(loginId);

        List<Stock.Market> markets = switch (marketType.toLowerCase()) {
            case "domestic" -> List.of(Stock.Market.KOSPI, Stock.Market.KOSDAQ);
            case "foreign" -> List.of(Stock.Market.NASDAQ, Stock.Market.NYSE, Stock.Market.AMEX);
            default -> throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        };

        List<Holding> holdings = holdingRepository.findByUserIdAndMarkets(user.getId(), markets);

        BigDecimal totalValue = holdings.stream()
                .map(h -> h.getCurrentValue(h.getStock().getCurrentPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInvestment = holdings.stream()
                .map(Holding::getTotalInvestment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfitLoss = totalValue.subtract(totalInvestment);

        BigDecimal totalProfitLossRate = totalInvestment.compareTo(BigDecimal.ZERO) > 0
                ? totalProfitLoss.divide(totalInvestment, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        List<PortfolioResponse.HoldingItem> holdingItems = holdings.stream()
                .map(h -> PortfolioResponse.HoldingItem.from(h, totalValue))
                .collect(Collectors.toList());

        return PortfolioResponse.Summary.builder()
                .totalValue(totalValue)
                .totalInvestment(totalInvestment)
                .totalProfitLoss(totalProfitLoss)
                .totalProfitLossRate(totalProfitLossRate)
                .holdingCount(holdings.size())
                .holdings(holdingItems)
                .build();
    }

    private User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
