import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { TrendingUp, TrendingDown, ChevronRight, Plus } from 'lucide-react';
import { Tabs, Card, StockListItem } from '@/components/common';
import { cn, formatNumber, formatPercent } from '@/utils';
import { useAuthStore } from '@/stores/authStore';
import type { Stock, MarketIndex } from '@/types';

// 목업 데이터
const mockPortfolio = {
  totalValue: 2450000,
  totalReturn: 35000,
  totalReturnRate: 1.45,
  domesticStocks: [
    { id: 1, code: '005930', name: '삼성전자', market: 'KOSPI' as const, currentPrice: 75000, previousClose: 73500, changePrice: 1500, changeRate: 2.04, volume: 15000000, quantity: 10 },
    { id: 2, code: '000660', name: 'SK하이닉스', market: 'KOSPI' as const, currentPrice: 128000, previousClose: 130000, changePrice: -2000, changeRate: -1.54, volume: 5000000, quantity: 5 },
    { id: 3, code: '035420', name: 'NAVER', market: 'KOSPI' as const, currentPrice: 195000, previousClose: 191500, changePrice: 3500, changeRate: 1.83, volume: 1200000, quantity: 3 },
    { id: 4, code: '051910', name: 'LG화학', market: 'KOSPI' as const, currentPrice: 420000, previousClose: 425000, changePrice: -5000, changeRate: -1.18, volume: 300000, quantity: 2 },
    { id: 5, code: '035720', name: '카카오', market: 'KOSPI' as const, currentPrice: 52000, previousClose: 51200, changePrice: 800, changeRate: 1.56, volume: 2500000, quantity: 8 },
  ],
  foreignStocks: [
    { id: 6, code: 'TSLA', name: '테슬라', market: 'NASDAQ' as const, currentPrice: 245.67, previousClose: 240.50, changePrice: 5.17, changeRate: 2.15, volume: 50000000, quantity: 10 },
    { id: 7, code: 'AAPL', name: '애플', market: 'NASDAQ' as const, currentPrice: 189.43, previousClose: 191.20, changePrice: -1.77, changeRate: -0.93, volume: 30000000, quantity: 5 },
  ],
};

const mockMarketIndices: MarketIndex[] = [
  { name: 'KOSPI', value: 2485.67, change: 12.34, changeRate: 0.50 },
  { name: 'KOSDAQ', value: 845.23, change: -0.38, changeRate: -0.04 },
  { name: 'S&P 500', value: 4567.89, change: 0.52, changeRate: 0.01 },
];

const mockHotStocks = [
  { id: 1, code: 'TSLA', name: '테슬라 (TSLA)', market: 'NASDAQ' as const, currentPrice: 245.67, previousClose: 240.50, changePrice: 5.17, changeRate: 2.15, volume: 50000000 },
  { id: 2, code: 'AAPL', name: '애플 (AAPL)', market: 'NASDAQ' as const, currentPrice: 189.43, previousClose: 191.20, changePrice: -1.77, changeRate: -0.93, volume: 30000000 },
  { id: 3, code: 'MSFT', name: '마이크로소프트 (MSFT)', market: 'NASDAQ' as const, currentPrice: 378.85, previousClose: 375.20, changePrice: 3.65, changeRate: 0.97, volume: 20000000 },
  { id: 4, code: 'NVDA', name: '엔비디아 (NVDA)', market: 'NASDAQ' as const, currentPrice: 875.30, previousClose: 860.15, changePrice: 15.15, changeRate: 1.76, volume: 25000000 },
  { id: 5, code: 'GOOGL', name: '구글 (GOOGL)', market: 'NASDAQ' as const, currentPrice: 142.56, previousClose: 143.20, changePrice: -0.64, changeRate: -0.45, volume: 15000000 },
];

const mainTabs = [
  { id: 'home', label: '증권홈' },
  { id: 'watchlist', label: '관심' },
  { id: 'discover', label: '발견' },
  { id: 'community', label: '소통' },
];

export default function HomePage() {
  const [activeTab, setActiveTab] = useState('home');
  const [chartTab, setChartTab] = useState('volume');
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();

  // 홈 탭 컨텐츠
  const renderHomeTab = () => (
    <div className="space-y-6">
      {/* 내 주식 요약 */}
      <Card className="p-5">
        <div className="flex items-center justify-between mb-4">
          <div>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">내 주식</p>
            <p className="text-2xl font-bold text-gray-900 dark:text-dark-text-primary">
              {formatNumber(mockPortfolio.totalValue)}원
            </p>
            <p className={cn(
              'text-sm font-medium',
              mockPortfolio.totalReturnRate >= 0 ? 'text-danger-500' : 'text-success-500'
            )}>
              {mockPortfolio.totalReturnRate >= 0 ? '+' : ''}{formatNumber(mockPortfolio.totalReturn)}원 ({formatPercent(mockPortfolio.totalReturnRate)})
            </p>
          </div>
          <ChevronRight className="w-6 h-6 text-gray-400" />
        </div>

        <div className="grid grid-cols-3 gap-4 py-3 border-t border-gray-100 dark:border-dark-border">
          <div className="text-center">
            <p className="text-xs text-gray-500 dark:text-dark-text-secondary">보유종목</p>
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">12개</p>
          </div>
          <div className="text-center border-x border-gray-100 dark:border-dark-border">
            <p className="text-xs text-gray-500 dark:text-dark-text-secondary">원화</p>
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">125,000원</p>
          </div>
          <div className="text-center">
            <p className="text-xs text-gray-500 dark:text-dark-text-secondary">달러</p>
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">$56.43</p>
          </div>
        </div>
      </Card>

      {/* 수익분석 링크 */}
      <button
        onClick={() => navigate('/portfolio')}
        className="w-full flex items-center justify-between px-4 py-3 bg-gray-50 dark:bg-dark-bg-secondary rounded-xl"
      >
        <span className="text-sm text-gray-600 dark:text-dark-text-secondary">가나다 순 ↑↓</span>
        <span className="text-sm text-primary-500 font-medium flex items-center gap-1">
          수익분석 <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* 국내주식 */}
      <div>
        <h3 className="text-sm font-medium text-gray-500 dark:text-dark-text-secondary mb-2 px-1">국내 주식</h3>
        <Card className="divide-y divide-gray-100 dark:divide-dark-border">
          {mockPortfolio.domesticStocks.map((stock) => (
            <StockListItem
              key={stock.id}
              stock={stock}
              showQuantity
              quantity={stock.quantity}
              onClick={() => navigate(`/stock/${stock.code}`)}
            />
          ))}
        </Card>
      </div>

      {/* 해외주식 */}
      <div>
        <h3 className="text-sm font-medium text-gray-500 dark:text-dark-text-secondary mb-2 px-1">해외 주식</h3>
        <Card className="divide-y divide-gray-100 dark:divide-dark-border">
          {mockPortfolio.foreignStocks.map((stock) => (
            <StockListItem
              key={stock.id}
              stock={stock}
              showQuantity
              quantity={stock.quantity}
              onClick={() => navigate(`/stock/${stock.code}`)}
            />
          ))}
        </Card>
      </div>
    </div>
  );

  // 관심 탭 컨텐츠
  const renderWatchlistTab = () => (
    <div className="space-y-4">
      <Tabs
        tabs={[
          { id: 'recent', label: '최근' },
          { id: 'foreign', label: '해외' },
          { id: 'domestic', label: '국내' },
          { id: 'group', label: '그룹' },
        ]}
        activeTab="domestic"
        onChange={() => {}}
        variant="pill"
      />
      
      <div className="flex justify-end">
        <button className="text-sm text-gray-500 dark:text-dark-text-secondary">그룹 편집</button>
      </div>

      <Card className="divide-y divide-gray-100 dark:divide-dark-border">
        {mockPortfolio.domesticStocks.map((stock) => (
          <StockListItem
            key={stock.id}
            stock={stock}
            showHeart
            isFavorite={stock.id % 2 === 0}
            onClick={() => navigate(`/stock/${stock.code}`)}
          />
        ))}
      </Card>

      {/* 관심 종목 추가 */}
      <button
        onClick={() => navigate('/search')}
        className="w-full flex items-center justify-center gap-2 py-4 border-2 border-dashed border-gray-200 dark:border-dark-border rounded-xl text-gray-500 dark:text-dark-text-secondary hover:bg-gray-50 dark:hover:bg-dark-bg-secondary transition-colors"
      >
        <Plus className="w-5 h-5" />
        <span>종목 추가하기</span>
      </button>

      {/* 비슷한 사람들의 관심 종목 */}
      <div className="mt-8">
        <div className="flex items-center justify-between mb-3">
          <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary">
            나와 비슷한 사람들의 관심 종목
          </h3>
          <button className="text-sm text-gray-500 dark:text-dark-text-secondary">
            더보기 <ChevronRight className="w-4 h-4 inline" />
          </button>
        </div>
        <Card className="divide-y divide-gray-100 dark:divide-dark-border">
          {mockHotStocks.slice(0, 4).map((stock) => (
            <StockListItem
              key={stock.id}
              stock={stock}
              showHeart
              onClick={() => navigate(`/stock/${stock.code}`)}
            />
          ))}
        </Card>
      </div>
    </div>
  );

  // 발견 탭 컨텐츠
  const renderDiscoverTab = () => (
    <div className="space-y-6">
      {/* 주요 지수 */}
      <Card className="p-4">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary">주요 지수</h3>
          <span className="text-xs text-gray-500 dark:text-dark-text-secondary">실시간</span>
        </div>
        <div className="space-y-3">
          {mockMarketIndices.map((index) => (
            <div key={index.name} className="flex items-center justify-between">
              <span className="text-gray-700 dark:text-dark-text-primary">{index.name}</span>
              <div className="text-right">
                <span className="font-semibold text-gray-900 dark:text-dark-text-primary">
                  {formatNumber(index.value)}
                </span>
                <span className={cn(
                  'text-sm ml-2',
                  index.changeRate >= 0 ? 'text-danger-500' : 'text-success-500'
                )}>
                  {index.changeRate >= 0 ? '+' : ''}{index.change.toFixed(2)} ({formatPercent(index.changeRate)})
                </span>
              </div>
            </div>
          ))}
        </div>
      </Card>

      {/* 실시간 차트 */}
      <div>
        <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-3">실시간 차트</h3>
        <Tabs
          tabs={[
            { id: 'volume', label: '거래대금' },
            { id: 'amount', label: '거래량' },
            { id: 'up', label: '급상승' },
            { id: 'down', label: '급하락' },
          ]}
          activeTab={chartTab}
          onChange={setChartTab}
        />
        
        <div className="flex justify-end mt-2 mb-3">
          <div className="flex gap-2 text-sm">
            <button className="px-3 py-1 bg-gray-100 dark:bg-dark-bg-secondary rounded-full text-gray-700 dark:text-dark-text-primary">
              국내
            </button>
            <button className="px-3 py-1 text-gray-500 dark:text-dark-text-secondary">해외</button>
          </div>
        </div>

        <Card className="divide-y divide-gray-100 dark:divide-dark-border">
          {mockHotStocks.map((stock, index) => (
            <div
              key={stock.id}
              className="flex items-center py-3 px-2 hover:bg-gray-50 dark:hover:bg-dark-bg-primary cursor-pointer"
              onClick={() => navigate(`/stock/${stock.code}`)}
            >
              <span className="w-6 text-center text-gray-500 dark:text-dark-text-secondary font-medium">
                {index + 1}
              </span>
              <div className="flex-1 ml-3">
                <p className="font-medium text-gray-900 dark:text-dark-text-primary">{stock.name}</p>
                <p className="text-sm text-gray-500 dark:text-dark-text-secondary">{stock.market}</p>
              </div>
              <div className="text-right">
                <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
                  ${stock.currentPrice.toFixed(2)}
                </p>
                <p className={cn(
                  'text-sm',
                  stock.changeRate >= 0 ? 'text-danger-500' : 'text-success-500'
                )}>
                  {formatPercent(stock.changeRate)}
                </p>
              </div>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                }}
                className="ml-3"
              >
                {index === 1 || index === 3 ? (
                  <span className="text-danger-500">♥</span>
                ) : (
                  <span className="text-gray-300">♡</span>
                )}
              </button>
            </div>
          ))}
        </Card>
      </div>
    </div>
  );

  // 소통 탭 컨텐츠
  const renderCommunityTab = () => (
    <div className="space-y-4">
      <p className="text-center text-gray-500 dark:text-dark-text-secondary py-10">
        커뮤니티 기능은 준비 중입니다.
      </p>
    </div>
  );

  const renderContent = () => {
    switch (activeTab) {
      case 'home':
        return renderHomeTab();
      case 'watchlist':
        return renderWatchlistTab();
      case 'discover':
        return renderDiscoverTab();
      case 'community':
        return renderCommunityTab();
      default:
        return renderHomeTab();
    }
  };

  // 로그인 안된 경우 온보딩 화면
  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-white dark:bg-dark-bg-primary">
        <div className="px-4 py-6">
          <Tabs
            tabs={mainTabs}
            activeTab={activeTab}
            onChange={setActiveTab}
          />
        </div>
        
        <div className="px-4">
          {/* 로그인 유도 카드 */}
          <Card className="p-6 text-center mb-6">
            <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-3xl">📈</span>
            </div>
            <h2 className="text-lg font-bold text-gray-900 dark:text-dark-text-primary mb-2">
              모의 투자군
            </h2>
            <p className="text-primary-500 font-medium mb-4">안전하게 시작하세요</p>
            <p className="text-2xl font-bold text-gray-900 dark:text-dark-text-primary mb-4">
              ₩10,000,000
            </p>
            <div className="space-y-2 text-sm text-left mb-6">
              <p className="flex items-center gap-2 text-gray-600 dark:text-dark-text-secondary">
                <span className="text-green-500">✓</span> 실제 주식 시장과 동일하게 운영
              </p>
              <p className="flex items-center gap-2 text-gray-600 dark:text-dark-text-secondary">
                <span className="text-green-500">✓</span> 국내 상장된 모든 종목
              </p>
              <p className="flex items-center gap-2 text-gray-600 dark:text-dark-text-secondary">
                <span className="text-green-500">✓</span> 위험부담 없이 투자 학습
              </p>
            </div>
            <button
              onClick={() => navigate('/login')}
              className="w-full btn-primary"
            >
              로그인하기
            </button>
          </Card>

          {/* 발견 탭 컨텐츠 미리보기 */}
          {renderDiscoverTab()}
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-light-bg-primary dark:bg-dark-bg-primary">
      <div className="px-4 py-3">
        <Tabs
          tabs={mainTabs}
          activeTab={activeTab}
          onChange={setActiveTab}
        />
      </div>
      
      <div className="px-4 pb-6">
        {renderContent()}
      </div>
    </div>
  );
}
