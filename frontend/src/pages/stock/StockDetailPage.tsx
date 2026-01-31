import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ChevronLeft, Heart, MoreHorizontal, ChevronRight } from 'lucide-react';
import { Tabs, Button, Card, Badge } from '@/components/common';
import { cn, formatNumber, formatPercent } from '@/utils';
import type { StockDetail, Orderbook } from '@/types';

// 목업 데이터
const mockStock: StockDetail = {
  id: 1,
  code: '005930',
  name: '삼성전자',
  market: 'KOSPI',
  currentPrice: 71500,
  previousClose: 70000,
  changePrice: 1500,
  changeRate: 2.14,
  volume: 15234567,
  high52Week: 85000,
  low52Week: 58000,
  per: 15.2,
  pbr: 1.3,
  eps: 4700,
  dividendYield: 2.5,
};

const mockOrderbook: Orderbook = {
  asks: [
    { price: 71900, quantity: 15130, type: 'ask' },
    { price: 71800, quantity: 12980, type: 'ask' },
  ],
  bids: [
    { price: 71500, quantity: 1250, type: 'bid' },
    { price: 71400, quantity: 890, type: 'bid' },
    { price: 71300, quantity: 2100, type: 'bid' },
    { price: 71200, quantity: 1560, type: 'bid' },
    { price: 71100, quantity: 860, type: 'bid' },
    { price: 71000, quantity: 3200, type: 'bid' },
    { price: 70900, quantity: 2800, type: 'bid' },
    { price: 70800, quantity: 1800, type: 'bid' },
    { price: 70700, quantity: 2450, type: 'bid' },
    { price: 70600, quantity: 1680, type: 'bid' },
    { price: 70500, quantity: 3100, type: 'bid' },
    { price: 70400, quantity: 2200, type: 'bid' },
  ],
};

const mockChartData = [
  { date: '9:00', price: 70000 },
  { date: '10:00', price: 70500 },
  { date: '11:00', price: 71200 },
  { date: '12:00', price: 70800 },
  { date: '13:00', price: 71000 },
  { date: '14:00', price: 71500 },
  { date: '15:00', price: 71500 },
];

const mockInvestorData = {
  foreigners: { buy: 45, sell: 55 },
  institutions: { buy: 60, sell: 40 },
  individuals: { buy: 35, sell: 65 },
};

const mockComments = [
  { id: 1, user: '익명', content: '삼성이야 뭐 믿고 간다~', time: '더보기' },
];

export default function StockDetailPage() {
  const { code } = useParams<{ code: string }>();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('chart');
  const [chartPeriod, setChartPeriod] = useState('1D');
  const [isFavorite, setIsFavorite] = useState(false);
  const [isOwned, setIsOwned] = useState(true); // 보유 여부

  const stock = mockStock;
  const priceColor = stock.changeRate >= 0 ? 'text-danger-500' : 'text-success-500';

  return (
    <div className="min-h-screen bg-white dark:bg-dark-bg-primary">
      {/* Header */}
      <div className="sticky top-0 bg-white dark:bg-dark-bg-primary z-10 border-b border-gray-100 dark:border-dark-border">
        <div className="flex items-center justify-between px-4 h-14">
          <button onClick={() => navigate(-1)} className="p-2 -ml-2">
            <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
          </button>
          <h1 className="font-semibold text-gray-900 dark:text-dark-text-primary">{stock.name}</h1>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setIsFavorite(!isFavorite)}
              className="p-2"
            >
              <Heart className={cn('w-6 h-6', isFavorite ? 'fill-danger-500 text-danger-500' : 'text-gray-400')} />
            </button>
            <button className="p-2">
              <MoreHorizontal className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
            </button>
          </div>
        </div>
      </div>

      <div className="px-4 py-4">
        {/* 가격 정보 */}
        <div className="mb-6">
          <div className="flex items-baseline gap-2">
            <span className="text-3xl font-bold text-gray-900 dark:text-dark-text-primary">
              {formatNumber(stock.currentPrice)}원
            </span>
          </div>
          <p className={cn('text-lg font-medium', priceColor)}>
            {stock.changeRate >= 0 ? '▲' : '▼'} {Math.abs(stock.changePrice)} ({formatPercent(stock.changeRate)})
          </p>
        </div>

        {/* 탭 */}
        <Tabs
          tabs={[
            { id: 'chart', label: '차트' },
            { id: 'quote', label: '호가' },
            { id: 'news', label: '뉴스/시세' },
            { id: 'info', label: '종목정보' },
            { id: 'community', label: '소통' },
          ]}
          activeTab={activeTab}
          onChange={setActiveTab}
        />

        {/* 탭 컨텐츠 */}
        <div className="mt-4">
          {activeTab === 'chart' && (
            <div className="space-y-4">
              {/* 간단한 차트 시각화 */}
              <div className="h-48 bg-gray-50 dark:bg-dark-bg-secondary rounded-xl flex items-end justify-around p-4">
                {mockChartData.map((data, index) => (
                  <div key={index} className="flex flex-col items-center gap-1">
                    <div
                      className={cn(
                        'w-6 rounded-t',
                        data.price > 70500 ? 'bg-danger-500' : 'bg-success-500'
                      )}
                      style={{ height: `${(data.price - 69500) / 30}px` }}
                    />
                    <span className="text-xs text-gray-500">{data.date.split(':')[0]}</span>
                  </div>
                ))}
              </div>

              {/* 기간 선택 */}
              <div className="flex gap-2">
                {['1D', '1W', '1개월', '3개월', '1Y', '전체'].map((period) => (
                  <button
                    key={period}
                    onClick={() => setChartPeriod(period)}
                    className={cn(
                      'px-3 py-1.5 rounded-full text-sm font-medium transition-colors',
                      chartPeriod === period
                        ? 'bg-primary-500 text-white'
                        : 'bg-gray-100 dark:bg-dark-bg-secondary text-gray-600 dark:text-dark-text-secondary'
                    )}
                  >
                    {period}
                  </button>
                ))}
              </div>

              {/* 호가 */}
              <Card className="p-4">
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-3">호가</h3>
                <p className="text-sm text-gray-500 dark:text-dark-text-secondary">
                  최고가 수량이 1.55만, 전 경기입
                </p>
              </Card>

              {/* 주문내역 */}
              <div>
                <div className="flex items-center justify-between mb-3">
                  <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary">주문내역</h3>
                  <ChevronRight className="w-5 h-5 text-gray-400" />
                </div>
              </div>

              {/* 투자자 동향 */}
              <Card className="p-4">
                <div className="flex items-center justify-between mb-4">
                  <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary">투자자 동향</h3>
                  <span className="text-xs text-gray-500 dark:text-dark-text-secondary">2만명+</span>
                </div>
                <div className="space-y-3">
                  {[
                    { label: '외국인', ...mockInvestorData.foreigners },
                    { label: '기관', ...mockInvestorData.institutions },
                    { label: '개인', ...mockInvestorData.individuals },
                  ].map((item) => (
                    <div key={item.label} className="flex items-center gap-3">
                      <span className="w-12 text-sm text-gray-600 dark:text-dark-text-secondary">{item.label}</span>
                      <div className="flex-1 h-2 bg-gray-100 dark:bg-dark-border rounded-full overflow-hidden flex">
                        <div
                          className="bg-danger-500 h-full"
                          style={{ width: `${item.buy}%` }}
                        />
                        <div
                          className="bg-success-500 h-full"
                          style={{ width: `${item.sell}%` }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
                <button className="w-full text-center text-sm text-gray-500 dark:text-dark-text-secondary mt-3">
                  더보기 <ChevronRight className="w-4 h-4 inline" />
                </button>
              </Card>

              {/* 소통방 */}
              <Card className="p-4">
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-3">소통방</h3>
                <div className="flex items-center gap-3 p-3 bg-gray-50 dark:bg-dark-bg-primary rounded-lg">
                  <div className="w-8 h-8 bg-primary-500 rounded-full flex items-center justify-center text-white text-sm font-medium">
                    익
                  </div>
                  <span className="flex-1 text-sm text-gray-600 dark:text-dark-text-secondary">
                    삼성이야 뭐 믿고 간다~
                  </span>
                </div>
                <button className="w-full text-center text-sm text-gray-500 dark:text-dark-text-secondary mt-3">
                  더보기 <ChevronRight className="w-4 h-4 inline" />
                </button>
              </Card>

              {/* Investory 정보 */}
              <Card className="p-4 bg-primary-50 dark:bg-primary-900/20 border-primary-100 dark:border-primary-800">
                <div className="flex items-center gap-2 mb-2">
                  <div className="w-6 h-6 bg-primary-500 rounded flex items-center justify-center">
                    <span className="text-white font-bold text-xs">W</span>
                  </div>
                  <span className="font-semibold text-primary-700 dark:text-primary-300">Investory</span>
                </div>
                <p className="text-sm text-gray-600 dark:text-dark-text-secondary leading-relaxed">
                  Investory에서 제공하는 서비스는 모의 투자 거래로 실제 매매가 이루어 지지 않습니다.
                  모의 투자를 통해 거래 및 투자 학습을 진행하시고 손쉽게 투자 정보를 탐색할 수 있습니다.
                </p>
              </Card>
            </div>
          )}

          {activeTab === 'quote' && (
            <div className="space-y-4">
              <Card className="p-0 overflow-hidden">
                {/* 매도 호가 */}
                {mockOrderbook.asks.slice().reverse().map((entry, index) => (
                  <div key={`ask-${index}`} className="flex items-center border-b border-gray-100 dark:border-dark-border">
                    <div className="flex-1 py-2 px-3 text-right">
                      <span className="text-success-500 font-medium">{formatNumber(entry.price)}</span>
                    </div>
                    <div className="w-20 py-2 px-3 bg-success-50 dark:bg-success-900/20 text-right">
                      <span className="text-sm text-success-600 dark:text-success-400">{formatNumber(entry.quantity)}</span>
                    </div>
                  </div>
                ))}
                
                {/* 현재가 */}
                <div className="flex items-center bg-gray-100 dark:bg-dark-bg-secondary border-y-2 border-gray-300 dark:border-dark-border">
                  <div className="flex-1 py-3 px-3 text-center">
                    <span className={cn('font-bold text-lg', priceColor)}>
                      {formatNumber(stock.currentPrice)}원
                    </span>
                  </div>
                </div>
                
                {/* 매수 호가 */}
                {mockOrderbook.bids.map((entry, index) => (
                  <div key={`bid-${index}`} className="flex items-center border-b border-gray-100 dark:border-dark-border last:border-b-0">
                    <div className="w-20 py-2 px-3 bg-danger-50 dark:bg-danger-900/20">
                      <span className="text-sm text-danger-600 dark:text-danger-400">{formatNumber(entry.quantity)}</span>
                    </div>
                    <div className="flex-1 py-2 px-3">
                      <span className="text-danger-500 font-medium">{formatNumber(entry.price)}</span>
                    </div>
                  </div>
                ))}
              </Card>
            </div>
          )}

          {activeTab === 'info' && (
            <div className="space-y-4">
              <Card className="p-4">
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-4">기본 정보</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-gray-500 dark:text-dark-text-secondary">PER</p>
                    <p className="font-semibold text-gray-900 dark:text-dark-text-primary">{stock.per}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-dark-text-secondary">PBR</p>
                    <p className="font-semibold text-gray-900 dark:text-dark-text-primary">{stock.pbr}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-dark-text-secondary">EPS</p>
                    <p className="font-semibold text-gray-900 dark:text-dark-text-primary">{formatNumber(stock.eps || 0)}원</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-dark-text-secondary">배당수익률</p>
                    <p className="font-semibold text-gray-900 dark:text-dark-text-primary">{stock.dividendYield}%</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-dark-text-secondary">52주 최고</p>
                    <p className="font-semibold text-danger-500">{formatNumber(stock.high52Week || 0)}원</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-dark-text-secondary">52주 최저</p>
                    <p className="font-semibold text-success-500">{formatNumber(stock.low52Week || 0)}원</p>
                  </div>
                </div>
              </Card>
            </div>
          )}
        </div>
      </div>

      {/* 하단 버튼 */}
      <div className="fixed bottom-0 left-0 right-0 p-4 bg-white dark:bg-dark-bg-primary border-t border-gray-100 dark:border-dark-border safe-bottom">
        <div className="max-w-lg mx-auto flex gap-3">
          {isOwned ? (
            <>
              <Button
                variant="outline"
                fullWidth
                onClick={() => navigate(`/trade/${code}?type=sell`)}
              >
                판매하기
              </Button>
              <Button
                variant="primary"
                fullWidth
                onClick={() => navigate(`/trade/${code}?type=buy`)}
              >
                구매하기
              </Button>
            </>
          ) : (
            <Button
              variant="primary"
              fullWidth
              onClick={() => navigate(`/trade/${code}?type=buy`)}
            >
              구매하기
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}
