import { useState, useEffect } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { ChevronLeft, Minus, Plus } from 'lucide-react';
import { Button, Tabs, Card } from '@/components/common';
import { cn, formatNumber } from '@/utils';
import type { Orderbook } from '@/types';

// 목업 데이터
const mockStock = {
  code: '005930',
  name: '삼성전자',
  currentPrice: 75000,
  changePrice: 1500,
  changeRate: 2.04,
};

const mockOrderbook: Orderbook = {
  asks: [
    { price: 76500, quantity: 1250, type: 'ask' as const },
    { price: 76400, quantity: 890, type: 'ask' as const },
    { price: 76300, quantity: 2100, type: 'ask' as const },
    { price: 76200, quantity: 1560, type: 'ask' as const },
    { price: 76100, quantity: 860, type: 'ask' as const },
    { price: 76000, quantity: 3200, type: 'ask' as const },
    { price: 75900, quantity: 2800, type: 'ask' as const },
    { price: 75800, quantity: 1800, type: 'ask' as const },
    { price: 75700, quantity: 2450, type: 'ask' as const },
    { price: 75600, quantity: 1680, type: 'ask' as const },
    { price: 75500, quantity: 3100, type: 'ask' as const },
    { price: 75400, quantity: 2200, type: 'ask' as const },
  ],
  bids: [
    { price: 75000, quantity: 1250, type: 'bid' as const },
    { price: 74900, quantity: 890, type: 'bid' as const },
    { price: 74800, quantity: 2100, type: 'bid' as const },
    { price: 74700, quantity: 1560, type: 'bid' as const },
    { price: 74600, quantity: 860, type: 'bid' as const },
    { price: 74500, quantity: 3200, type: 'bid' as const },
  ],
};

const mockAccount = {
  balance: 1250000,
  availableBalance: 1174999,
};

type OrderType = 'buy' | 'sell' | 'cancel';
type PriceType = 'market' | 'limit';

export default function TradePage() {
  const { code } = useParams<{ code: string }>();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [orderType, setOrderType] = useState<OrderType>(
    (searchParams.get('type') as OrderType) || 'buy'
  );
  const [priceType, setPriceType] = useState<PriceType>('market');
  const [quantity, setQuantity] = useState(1);
  const [limitPrice, setLimitPrice] = useState(mockStock.currentPrice);

  const stock = mockStock;
  const priceColor = stock.changeRate >= 0 ? 'text-danger-500' : 'text-success-500';
  
  const totalAmount = priceType === 'market' 
    ? stock.currentPrice * quantity 
    : limitPrice * quantity;

  const maxQuantity = priceType === 'market'
    ? Math.floor(mockAccount.availableBalance / stock.currentPrice)
    : Math.floor(mockAccount.availableBalance / limitPrice);

  const handleQuantityChange = (delta: number) => {
    const newQuantity = Math.max(1, Math.min(quantity + delta, maxQuantity));
    setQuantity(newQuantity);
  };

  const handleSubmit = () => {
    // 실제 구현 시 주문 API 호출
    alert(`${orderType === 'buy' ? '구매' : '판매'} 주문이 접수되었습니다.`);
    navigate(-1);
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-dark-bg-primary">
      {/* Header */}
      <div className="sticky top-0 bg-white dark:bg-dark-bg-secondary z-10 border-b border-gray-100 dark:border-dark-border">
        <div className="flex items-center justify-between px-4 h-14">
          <button onClick={() => navigate(-1)} className="p-2 -ml-2">
            <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
          </button>
          <span className="font-medium text-gray-500 dark:text-dark-text-secondary">호가창</span>
          <div className="w-10" />
        </div>
      </div>

      <div className="flex">
        {/* 호가창 */}
        <div className="w-1/2 bg-white dark:bg-dark-bg-secondary">
          <div className="sticky top-14">
            <div className="flex border-b border-gray-100 dark:border-dark-border">
              <div className="flex-1 py-2 text-center text-sm font-medium text-gray-500">가격</div>
              <div className="flex-1 py-2 text-center text-sm font-medium text-gray-500">수량</div>
            </div>
            
            {/* 매도 호가 */}
            {mockOrderbook.asks.slice().reverse().map((entry, index) => (
              <button
                key={`ask-${index}`}
                onClick={() => {
                  setPriceType('limit');
                  setLimitPrice(entry.price);
                }}
                className="w-full flex hover:bg-gray-50 dark:hover:bg-dark-bg-primary"
              >
                <div className="flex-1 py-1.5 px-2 text-left">
                  <span className={cn(
                    'font-medium text-sm',
                    entry.price > stock.currentPrice ? 'text-danger-500' : 'text-success-500'
                  )}>
                    {formatNumber(entry.price)}
                  </span>
                  {entry.price === stock.currentPrice && (
                    <span className="text-xs ml-1 text-gray-400">▼</span>
                  )}
                </div>
                <div className="flex-1 py-1.5 px-2 bg-success-50 dark:bg-success-900/20">
                  <span className="text-sm text-success-600 dark:text-success-400">
                    {formatNumber(entry.quantity)}
                  </span>
                </div>
              </button>
            ))}
            
            {/* 현재가 표시 */}
            <div className="py-2 px-2 bg-gray-100 dark:bg-dark-bg-primary border-y border-gray-200 dark:border-dark-border">
              <span className={cn('font-bold', priceColor)}>
                {formatNumber(stock.currentPrice)}원
              </span>
            </div>
            
            {/* 매수 호가 */}
            {mockOrderbook.bids.map((entry, index) => (
              <button
                key={`bid-${index}`}
                onClick={() => {
                  setPriceType('limit');
                  setLimitPrice(entry.price);
                }}
                className="w-full flex hover:bg-gray-50 dark:hover:bg-dark-bg-primary"
              >
                <div className="flex-1 py-1.5 px-2 bg-danger-50 dark:bg-danger-900/20">
                  <span className="text-sm text-danger-600 dark:text-danger-400">
                    {formatNumber(entry.quantity)}
                  </span>
                </div>
                <div className="flex-1 py-1.5 px-2 text-right">
                  <span className="text-sm font-medium text-danger-500">
                    {formatNumber(entry.price)}
                  </span>
                </div>
              </button>
            ))}

            {/* 하단 요약 */}
            <div className="p-3 border-t border-gray-100 dark:border-dark-border bg-white dark:bg-dark-bg-secondary">
              <div className="flex justify-between text-sm">
                <div className="text-center">
                  <p className="text-gray-500 dark:text-dark-text-secondary">구매 대기</p>
                  <p className="font-semibold text-danger-500">{formatNumber(12980)}</p>
                </div>
                <div className="text-center">
                  <p className="text-gray-500 dark:text-dark-text-secondary">판매대기</p>
                  <p className="font-semibold text-success-500">{formatNumber(15130)}</p>
                </div>
              </div>
              <div className="mt-2 text-center">
                <p className="text-sm text-gray-500 dark:text-dark-text-secondary">체결강도</p>
                <p className="font-bold text-danger-500">75.8%</p>
              </div>
            </div>
          </div>
        </div>

        {/* 주문 폼 */}
        <div className="w-1/2 p-4">
          {/* 종목 정보 */}
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 bg-primary-500 rounded-full flex items-center justify-center text-white font-semibold">
              {stock.name.charAt(0)}
            </div>
            <div>
              <p className="font-semibold text-gray-900 dark:text-dark-text-primary">{stock.name}</p>
              <p className="text-sm text-gray-500 dark:text-dark-text-secondary">{stock.code}</p>
            </div>
            <div className="ml-auto text-right">
              <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
                {formatNumber(stock.currentPrice)}원
              </p>
              <p className={cn('text-sm', priceColor)}>
                {stock.changeRate >= 0 ? '+' : ''}{formatNumber(stock.changePrice)} ({stock.changeRate}%)
              </p>
            </div>
          </div>

          {/* 주문 유형 */}
          <Card className="p-4 mb-4">
            <h3 className="text-sm font-medium text-gray-700 dark:text-dark-text-secondary mb-2">주문 유형</h3>
            <Tabs
              tabs={[
                { id: 'buy', label: '구매' },
                { id: 'sell', label: '판매' },
                { id: 'cancel', label: '취소' },
              ]}
              activeTab={orderType}
              onChange={(id) => setOrderType(id as OrderType)}
              variant="pill"
            />
          </Card>

          {/* 가격 설정 */}
          <Card className="p-4 mb-4">
            <h3 className="text-sm font-medium text-gray-700 dark:text-dark-text-secondary mb-2">가격 설정</h3>
            <Tabs
              tabs={[
                { id: 'market', label: '시장가' },
                { id: 'limit', label: '지정가' },
              ]}
              activeTab={priceType}
              onChange={(id) => setPriceType(id as PriceType)}
              variant="pill"
            />
            
            {priceType === 'limit' && (
              <div className="mt-3">
                <label className="text-sm text-gray-500 dark:text-dark-text-secondary">원하는 가격</label>
                <div className="mt-1 relative">
                  <input
                    type="number"
                    value={limitPrice}
                    onChange={(e) => setLimitPrice(Number(e.target.value))}
                    className="w-full input text-right pr-8"
                  />
                  <span className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500">원</span>
                </div>
              </div>
            )}

            {priceType === 'market' && (
              <div className="mt-3 p-3 bg-gray-50 dark:bg-dark-bg-primary rounded-lg">
                <p className="text-sm text-gray-600 dark:text-dark-text-secondary">
                  현재 시장가
                </p>
                <p className="text-xl font-bold text-gray-900 dark:text-dark-text-primary">
                  {formatNumber(stock.currentPrice)}원
                </p>
              </div>
            )}
          </Card>

          {/* 구매 수량 */}
          <Card className="p-4 mb-4">
            <h3 className="text-sm font-medium text-gray-700 dark:text-dark-text-secondary mb-2">
              {orderType === 'buy' ? '구매' : '판매'} 수량
            </h3>
            <div className="flex items-center justify-center gap-4 py-2">
              <button
                onClick={() => handleQuantityChange(-1)}
                className="w-10 h-10 flex items-center justify-center bg-gray-100 dark:bg-dark-bg-primary rounded-full"
                disabled={quantity <= 1}
              >
                <Minus className="w-5 h-5 text-gray-600 dark:text-dark-text-secondary" />
              </button>
              <div className="w-20 text-center">
                <span className="text-2xl font-bold text-gray-900 dark:text-dark-text-primary">{quantity}</span>
                <span className="text-gray-500 dark:text-dark-text-secondary ml-1">주</span>
              </div>
              <button
                onClick={() => handleQuantityChange(1)}
                className="w-10 h-10 flex items-center justify-center bg-gray-100 dark:bg-dark-bg-primary rounded-full"
                disabled={quantity >= maxQuantity}
              >
                <Plus className="w-5 h-5 text-gray-600 dark:text-dark-text-secondary" />
              </button>
            </div>
            <input
              type="range"
              min={1}
              max={Math.max(maxQuantity, 1)}
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value))}
              className="w-full mt-2"
            />
            <div className="flex justify-between text-xs text-gray-500 dark:text-dark-text-secondary mt-1">
              <span>1주</span>
              <span>{maxQuantity}주 (최대)</span>
            </div>
          </Card>

          {/* 주문 금액 */}
          <Card className="p-4 mb-4">
            <div className="space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-500 dark:text-dark-text-secondary">주문 금액</span>
                <span className="text-gray-900 dark:text-dark-text-primary">{formatNumber(totalAmount)}원</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-500 dark:text-dark-text-secondary">추정 금액</span>
                <span className="text-gray-900 dark:text-dark-text-primary">{formatNumber(totalAmount)}원</span>
              </div>
              <div className="flex justify-between text-lg font-bold pt-2 border-t border-gray-100 dark:border-dark-border">
                <span className="text-gray-700 dark:text-dark-text-secondary">총 결제금액</span>
                <span className={orderType === 'buy' ? 'text-danger-500' : 'text-success-500'}>
                  {formatNumber(totalAmount)}원
                </span>
              </div>
            </div>

            <div className="mt-4 pt-4 border-t border-gray-100 dark:border-dark-border space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-500 dark:text-dark-text-secondary">보유현금</span>
                <span className="text-gray-900 dark:text-dark-text-primary">{formatNumber(mockAccount.balance)}원</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-500 dark:text-dark-text-secondary">주문 후 잔액</span>
                <span className="text-gray-900 dark:text-dark-text-primary">
                  {formatNumber(mockAccount.availableBalance - totalAmount)}원
                </span>
              </div>
            </div>
          </Card>

          {/* 주문 버튼 */}
          <Button
            variant={orderType === 'buy' ? 'primary' : 'danger'}
            fullWidth
            onClick={handleSubmit}
            className={orderType === 'buy' ? 'bg-danger-500 hover:bg-danger-600' : ''}
          >
            {orderType === 'buy' ? '구매하기' : '판매하기'}
          </Button>
        </div>
      </div>
    </div>
  );
}
