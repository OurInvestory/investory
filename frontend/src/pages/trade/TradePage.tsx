import { useState, useEffect } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { ChevronLeft, Minus, Plus } from 'lucide-react';
import { Button, Tabs, Card } from '@/components/common';
import { cn, formatNumber } from '@/utils';
import { useStockDetail, useOrderbook, useCreateOrder } from '@/hooks/useApi';
import toast from 'react-hot-toast';
import type { Orderbook } from '@/types';

type OrderType = 'buy' | 'sell' | 'cancel';
type PriceType = 'market' | 'limit';

export default function TradePage() {
  const { code } = useParams<{ code: string }>();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const { data: stockData } = useStockDetail(code || '');
  const { data: orderbookData } = useOrderbook(code || '');
  const createOrder = useCreateOrder();

  const [orderType, setOrderType] = useState<OrderType>(
    (searchParams.get('type') as OrderType) || 'buy'
  );
  const [priceType, setPriceType] = useState<PriceType>('market');
  const [quantity, setQuantity] = useState(1);

  const stock = stockData ? {
    code: stockData.code,
    name: stockData.name,
    currentPrice: stockData.currentPrice,
    changePrice: stockData.changeAmount,
    changeRate: stockData.changeRate,
  } : {
    code: code || '',
    name: '濡쒕뵫 以?..',
    currentPrice: 0,
    changePrice: 0,
    changeRate: 0,
  };

  const [limitPrice, setLimitPrice] = useState(stock.currentPrice);

  useEffect(() => {
    if (stockData) {
      setLimitPrice(stockData.currentPrice);
    }
  }, [stockData]);

  // Build orderbook from API or fallback to empty
  const orderbook: Orderbook = orderbookData ? {
    asks: orderbookData.asks.map((e: any) => ({ price: e.price, quantity: e.quantity, type: 'ask' as const })),
    bids: orderbookData.bids.map((e: any) => ({ price: e.price, quantity: e.quantity, type: 'bid' as const })),
  } : { asks: [], bids: [] };

  // Starting balance for paper trading  
  const INITIAL_BALANCE = 10_000_000;
  const account = {
    balance: INITIAL_BALANCE,
    availableBalance: INITIAL_BALANCE,
  };

  const priceColor = stock.changeRate >= 0 ? 'text-danger-500' : 'text-success-500';
  
  const totalAmount = priceType === 'market' 
    ? stock.currentPrice * quantity 
    : limitPrice * quantity;

  const maxQuantity = stock.currentPrice > 0 ? (priceType === 'market'
    ? Math.floor(account.availableBalance / stock.currentPrice)
    : Math.floor(account.availableBalance / (limitPrice || 1))) : 1;

  const handleQuantityChange = (delta: number) => {
    const newQuantity = Math.max(1, Math.min(quantity + delta, maxQuantity));
    setQuantity(newQuantity);
  };

  const handleSubmit = async () => {
    try {
      await createOrder.mutateAsync({
        stockCode: code || '',
        side: orderType === 'buy' ? 'BUY' : 'SELL',
        orderType: priceType === 'market' ? 'MARKET' : 'LIMIT',
        quantity,
        price: priceType === 'limit' ? limitPrice : undefined,
      });
      toast.success(`${orderType === 'buy' ? '援щℓ' : '?먮ℓ'} 二쇰Ц??泥닿껐?섏뿀?듬땲??`);
      navigate(-1);
    } catch {
      toast.error('二쇰Ц 泥섎━???ㅽ뙣?덉뒿?덈떎.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-dark-bg-primary">
      {/* Header */}
      <div className="sticky top-0 bg-white dark:bg-dark-bg-secondary z-10 border-b border-gray-100 dark:border-dark-border">
        <div className="flex items-center justify-between px-4 h-14">
          <button onClick={() => navigate(-1)} className="p-2 -ml-2">
            <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
          </button>
          <span className="font-medium text-gray-500 dark:text-dark-text-secondary">?멸?李?/span>
          <div className="w-10" />
        </div>
      </div>

      <div className="flex">
        {/* ?멸?李?*/}
        <div className="w-1/2 bg-white dark:bg-dark-bg-secondary">
          <div className="sticky top-14">
            <div className="flex border-b border-gray-100 dark:border-dark-border">
              <div className="flex-1 py-2 text-center text-sm font-medium text-gray-500">媛寃?/div>
              <div className="flex-1 py-2 text-center text-sm font-medium text-gray-500">?섎웾</div>
            </div>
            
            {/* 留ㅻ룄 ?멸? */}
            {orderbook.asks.slice().reverse().map((entry, index) => (
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
                    <span className="text-xs ml-1 text-gray-400">??/span>
                  )}
                </div>
                <div className="flex-1 py-1.5 px-2 bg-success-50 dark:bg-success-900/20">
                  <span className="text-sm text-success-600 dark:text-success-400">
                    {formatNumber(entry.quantity)}
                  </span>
                </div>
              </button>
            ))}
            
            {/* ?꾩옱媛 ?쒖떆 */}
            <div className="py-2 px-2 bg-gray-100 dark:bg-dark-bg-primary border-y border-gray-200 dark:border-dark-border">
              <span className={cn('font-bold', priceColor)}>
                {formatNumber(stock.currentPrice)}??
              </span>
            </div>
            
            {/* 留ㅼ닔 ?멸? */}
            {orderbook.bids.map((entry, index) => (
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

            {/* ?섎떒 ?붿빟 */}
            <div className="p-3 border-t border-gray-100 dark:border-dark-border bg-white dark:bg-dark-bg-secondary">
              <div className="flex justify-between text-sm">
                <div className="text-center">
                  <p className="text-gray-500 dark:text-dark-text-secondary">援щℓ ?湲?/p>
                  <p className="font-semibold text-danger-500">{formatNumber(12980)}</p>
                </div>
                <div className="text-center">
                  <p className="text-gray-500 dark:text-dark-text-secondary">?먮ℓ?湲?/p>
                  <p className="font-semibold text-success-500">{formatNumber(15130)}</p>
                </div>
              </div>
              <div className="mt-2 text-center">
                <p className="text-sm text-gray-500 dark:text-dark-text-secondary">泥닿껐媛뺣룄</p>
                <p className="font-bold text-danger-500">75.8%</p>
              </div>
            </div>
          </div>
        </div>

        {/* 二쇰Ц ??*/}
        <div className="w-1/2 p-4">
          {/* 醫낅ぉ ?뺣낫 */}
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
                {formatNumber(stock.currentPrice)}??
              </p>
              <p className={cn('text-sm', priceColor)}>
                {stock.changeRate >= 0 ? '+' : ''}{formatNumber(stock.changePrice)} ({stock.changeRate}%)
              </p>
            </div>
          </div>

          {/* 二쇰Ц ?좏삎 */}
          <Card className="p-4 mb-4">
            <h3 className="text-sm font-medium text-gray-700 dark:text-dark-text-secondary mb-2">二쇰Ц ?좏삎</h3>
            <Tabs
              tabs={[
                { id: 'buy', label: '援щℓ' },
                { id: 'sell', label: '?먮ℓ' },
                { id: 'cancel', label: '痍⑥냼' },
              ]}
              activeTab={orderType}
              onChange={(id) => setOrderType(id as OrderType)}
              variant="pill"
            />
          </Card>

          {/* 媛寃??ㅼ젙 */}
          <Card className="p-4 mb-4">
            <h3 className="text-sm font-medium text-gray-700 dark:text-dark-text-secondary mb-2">媛寃??ㅼ젙</h3>
            <Tabs
              tabs={[
                { id: 'market', label: '?쒖옣媛' },
                { id: 'limit', label: '吏?뺢?' },
              ]}
              activeTab={priceType}
              onChange={(id) => setPriceType(id as PriceType)}
              variant="pill"
            />
            
            {priceType === 'limit' && (
              <div className="mt-3">
                <label className="text-sm text-gray-500 dark:text-dark-text-secondary">?먰븯??媛寃?/label>
                <div className="mt-1 relative">
                  <input
                    type="number"
                    value={limitPrice}
                    onChange={(e) => setLimitPrice(Number(e.target.value))}
                    className="w-full input text-right pr-8"
                  />
                  <span className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500">??/span>
                </div>
              </div>
            )}

            {priceType === 'market' && (
              <div className="mt-3 p-3 bg-gray-50 dark:bg-dark-bg-primary rounded-lg">
                <p className="text-sm text-gray-600 dark:text-dark-text-secondary">
                  ?꾩옱 ?쒖옣媛
                </p>
                <p className="text-xl font-bold text-gray-900 dark:text-dark-text-primary">
                  {formatNumber(stock.currentPrice)}??
                </p>
              </div>
            )}
          </Card>

          {/* 援щℓ ?섎웾 */}
          <Card className="p-4 mb-4">
            <h3 className="text-sm font-medium text-gray-700 dark:text-dark-text-secondary mb-2">
              {orderType === 'buy' ? '援щℓ' : '?먮ℓ'} ?섎웾
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
                <span className="text-gray-500 dark:text-dark-text-secondary ml-1">二?/span>
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
              <span>1二?/span>
              <span>{maxQuantity}二?(理쒕?)</span>
            </div>
          </Card>

          {/* 二쇰Ц 湲덉븸 */}
          <Card className="p-4 mb-4">
            <div className="space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-500 dark:text-dark-text-secondary">二쇰Ц 湲덉븸</span>
                <span className="text-gray-900 dark:text-dark-text-primary">{formatNumber(totalAmount)}??/span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-500 dark:text-dark-text-secondary">異붿젙 湲덉븸</span>
                <span className="text-gray-900 dark:text-dark-text-primary">{formatNumber(totalAmount)}??/span>
              </div>
              <div className="flex justify-between text-lg font-bold pt-2 border-t border-gray-100 dark:border-dark-border">
                <span className="text-gray-700 dark:text-dark-text-secondary">珥?寃곗젣湲덉븸</span>
                <span className={orderType === 'buy' ? 'text-danger-500' : 'text-success-500'}>
                  {formatNumber(totalAmount)}??
                </span>
              </div>
            </div>

            <div className="mt-4 pt-4 border-t border-gray-100 dark:border-dark-border space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-500 dark:text-dark-text-secondary">蹂댁쑀?꾧툑</span>
                <span className="text-gray-900 dark:text-dark-text-primary">{formatNumber(account.balance)}??/span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-500 dark:text-dark-text-secondary">二쇰Ц ???붿븸</span>
                <span className="text-gray-900 dark:text-dark-text-primary">
                  {formatNumber(account.availableBalance - totalAmount)}??
                </span>
              </div>
            </div>
          </Card>

          {/* 二쇰Ц 踰꾪듉 */}
          <Button
            variant={orderType === 'buy' ? 'primary' : 'danger'}
            fullWidth
            onClick={handleSubmit}
            className={orderType === 'buy' ? 'bg-danger-500 hover:bg-danger-600' : ''}
          >
            {orderType === 'buy' ? '援щℓ?섍린' : '?먮ℓ?섍린'}
          </Button>
        </div>
      </div>
    </div>
  );
}
