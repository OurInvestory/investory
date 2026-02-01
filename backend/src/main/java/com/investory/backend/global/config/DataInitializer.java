package com.investory.backend.global.config;

import com.investory.backend.domain.reward.entity.Achievement;
import com.investory.backend.domain.reward.repository.AchievementRepository;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StockRepository stockRepository;
    private final AchievementRepository achievementRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initStocks();
        initAchievements();
        log.info("초기 데이터 로딩 완료");
    }

    private void initStocks() {
        if (stockRepository.count() > 0) {
            return;
        }

        List<Stock> stocks = List.of(
                // 국내 주식 - KOSPI
                Stock.builder()
                        .code("005930").name("삼성전자").englishName("Samsung Electronics")
                        .market(Stock.Market.KOSPI).sector("전기전자")
                        .currentPrice(new BigDecimal("71500")).previousClose(new BigDecimal("71000"))
                        .changeRate(new BigDecimal("0.70")).changeAmount(new BigDecimal("500"))
                        .volume(15000000L).marketCap(4270000000000000L)
                        .build(),
                Stock.builder()
                        .code("000660").name("SK하이닉스").englishName("SK Hynix")
                        .market(Stock.Market.KOSPI).sector("전기전자")
                        .currentPrice(new BigDecimal("178000")).previousClose(new BigDecimal("175000"))
                        .changeRate(new BigDecimal("1.71")).changeAmount(new BigDecimal("3000"))
                        .volume(3500000L).marketCap(1300000000000000L)
                        .build(),
                Stock.builder()
                        .code("035420").name("NAVER").englishName("NAVER Corp")
                        .market(Stock.Market.KOSPI).sector("서비스업")
                        .currentPrice(new BigDecimal("215000")).previousClose(new BigDecimal("218000"))
                        .changeRate(new BigDecimal("-1.38")).changeAmount(new BigDecimal("-3000"))
                        .volume(800000L).marketCap(350000000000000L)
                        .build(),
                Stock.builder()
                        .code("035720").name("카카오").englishName("Kakao Corp")
                        .market(Stock.Market.KOSPI).sector("서비스업")
                        .currentPrice(new BigDecimal("45000")).previousClose(new BigDecimal("44500"))
                        .changeRate(new BigDecimal("1.12")).changeAmount(new BigDecimal("500"))
                        .volume(2500000L).marketCap(200000000000000L)
                        .build(),
                Stock.builder()
                        .code("005380").name("현대차").englishName("Hyundai Motor")
                        .market(Stock.Market.KOSPI).sector("운수장비")
                        .currentPrice(new BigDecimal("245000")).previousClose(new BigDecimal("242000"))
                        .changeRate(new BigDecimal("1.24")).changeAmount(new BigDecimal("3000"))
                        .volume(1200000L).marketCap(520000000000000L)
                        .build(),
                
                // 국내 주식 - KOSDAQ
                Stock.builder()
                        .code("263750").name("펄어비스").englishName("Pearl Abyss")
                        .market(Stock.Market.KOSDAQ).sector("게임")
                        .currentPrice(new BigDecimal("42500")).previousClose(new BigDecimal("43000"))
                        .changeRate(new BigDecimal("-1.16")).changeAmount(new BigDecimal("-500"))
                        .volume(500000L).marketCap(20000000000000L)
                        .build(),
                Stock.builder()
                        .code("293490").name("카카오게임즈").englishName("Kakao Games")
                        .market(Stock.Market.KOSDAQ).sector("게임")
                        .currentPrice(new BigDecimal("18500")).previousClose(new BigDecimal("18000"))
                        .changeRate(new BigDecimal("2.78")).changeAmount(new BigDecimal("500"))
                        .volume(3000000L).marketCap(16000000000000L)
                        .build(),
                
                // 해외 주식 - NASDAQ
                Stock.builder()
                        .code("AAPL").name("애플").englishName("Apple Inc")
                        .market(Stock.Market.NASDAQ).sector("Technology")
                        .currentPrice(new BigDecimal("185.50")).previousClose(new BigDecimal("184.00"))
                        .changeRate(new BigDecimal("0.82")).changeAmount(new BigDecimal("1.50"))
                        .volume(50000000L).marketCap(2900000000000L)
                        .build(),
                Stock.builder()
                        .code("MSFT").name("마이크로소프트").englishName("Microsoft Corp")
                        .market(Stock.Market.NASDAQ).sector("Technology")
                        .currentPrice(new BigDecimal("378.90")).previousClose(new BigDecimal("375.20"))
                        .changeRate(new BigDecimal("0.99")).changeAmount(new BigDecimal("3.70"))
                        .volume(25000000L).marketCap(2810000000000L)
                        .build(),
                Stock.builder()
                        .code("NVDA").name("엔비디아").englishName("NVIDIA Corp")
                        .market(Stock.Market.NASDAQ).sector("Technology")
                        .currentPrice(new BigDecimal("495.20")).previousClose(new BigDecimal("488.00"))
                        .changeRate(new BigDecimal("1.48")).changeAmount(new BigDecimal("7.20"))
                        .volume(45000000L).marketCap(1220000000000L)
                        .build(),
                Stock.builder()
                        .code("TSLA").name("테슬라").englishName("Tesla Inc")
                        .market(Stock.Market.NASDAQ).sector("Consumer Cyclical")
                        .currentPrice(new BigDecimal("248.50")).previousClose(new BigDecimal("252.00"))
                        .changeRate(new BigDecimal("-1.39")).changeAmount(new BigDecimal("-3.50"))
                        .volume(80000000L).marketCap(790000000000L)
                        .build(),
                Stock.builder()
                        .code("AMZN").name("아마존").englishName("Amazon.com Inc")
                        .market(Stock.Market.NASDAQ).sector("Consumer Cyclical")
                        .currentPrice(new BigDecimal("153.80")).previousClose(new BigDecimal("152.50"))
                        .changeRate(new BigDecimal("0.85")).changeAmount(new BigDecimal("1.30"))
                        .volume(35000000L).marketCap(1590000000000L)
                        .build(),
                Stock.builder()
                        .code("GOOGL").name("알파벳").englishName("Alphabet Inc")
                        .market(Stock.Market.NASDAQ).sector("Communication Services")
                        .currentPrice(new BigDecimal("141.20")).previousClose(new BigDecimal("140.80"))
                        .changeRate(new BigDecimal("0.28")).changeAmount(new BigDecimal("0.40"))
                        .volume(20000000L).marketCap(1780000000000L)
                        .build(),
                Stock.builder()
                        .code("META").name("메타").englishName("Meta Platforms Inc")
                        .market(Stock.Market.NASDAQ).sector("Communication Services")
                        .currentPrice(new BigDecimal("358.70")).previousClose(new BigDecimal("355.00"))
                        .changeRate(new BigDecimal("1.04")).changeAmount(new BigDecimal("3.70"))
                        .volume(12000000L).marketCap(920000000000L)
                        .build()
        );

        stockRepository.saveAll(stocks);
        log.info("샘플 주식 데이터 {} 건 초기화", stocks.size());
    }

    private void initAchievements() {
        if (achievementRepository.count() > 0) {
            return;
        }

        List<Achievement> achievements = List.of(
                Achievement.builder()
                        .code("FIRST_TRADE").name("첫 거래").description("첫 번째 주식 거래를 완료하세요")
                        .icon("Target").category(Achievement.AchievementCategory.TRADING)
                        .expReward(100).maxProgress(1).build(),
                Achievement.builder()
                        .code("PROFIT_10").name("수익 달성").description("총 수익률 10% 달성")
                        .icon("TrendingUp").category(Achievement.AchievementCategory.TRADING)
                        .expReward(500).build(),
                Achievement.builder()
                        .code("FIRST_POST").name("커뮤니티 참여").description("첫 번째 게시글 작성")
                        .icon("Users").category(Achievement.AchievementCategory.COMMUNITY)
                        .expReward(50).maxProgress(1).build(),
                Achievement.builder()
                        .code("DIVERSIFY_5").name("포트폴리오 다양화").description("5개 이상의 종목 보유")
                        .icon("Star").category(Achievement.AchievementCategory.PORTFOLIO)
                        .expReward(200).maxProgress(5).build(),
                Achievement.builder()
                        .code("LOGIN_30").name("장기 투자자").description("30일 연속 로그인")
                        .icon("Trophy").category(Achievement.AchievementCategory.STREAK)
                        .expReward(300).maxProgress(30).build(),
                Achievement.builder()
                        .code("PROFIT_1M").name("수익왕").description("단일 거래에서 100만원 이상 수익")
                        .icon("Trophy").category(Achievement.AchievementCategory.TRADING)
                        .expReward(1000).build(),
                Achievement.builder()
                        .code("WMTI_COMPLETE").name("투자 성향 분석").description("WMTI 테스트 완료")
                        .icon("Brain").category(Achievement.AchievementCategory.LEARNING)
                        .expReward(100).maxProgress(1).build(),
                Achievement.builder()
                        .code("TRADE_10").name("활발한 투자자").description("10번의 거래 완료")
                        .icon("Activity").category(Achievement.AchievementCategory.TRADING)
                        .expReward(150).maxProgress(10).build()
        );

        achievementRepository.saveAll(achievements);
        log.info("업적 데이터 {} 건 초기화", achievements.size());
    }
}
