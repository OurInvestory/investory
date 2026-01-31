import { cn, formatNumber, formatPercent, getPriceColorClass } from '@/utils';
import type { Stock } from '@/types';
import { Heart } from 'lucide-react';

interface StockListItemProps {
  stock: Stock;
  showHeart?: boolean;
  isFavorite?: boolean;
  onToggleFavorite?: () => void;
  onClick?: () => void;
  showQuantity?: boolean;
  quantity?: number;
  className?: string;
}

export default function StockListItem({
  stock,
  showHeart = false,
  isFavorite = false,
  onToggleFavorite,
  onClick,
  showQuantity = false,
  quantity,
  className,
}: StockListItemProps) {
  const priceColor = getPriceColorClass(stock.changeRate);

  return (
    <div
      className={cn(
        'flex items-center justify-between py-4 px-2 hover:bg-gray-50 dark:hover:bg-dark-bg-secondary rounded-lg cursor-pointer transition-colors',
        className
      )}
      onClick={onClick}
    >
      <div className="flex items-center gap-3">
        <div
          className={cn(
            'w-10 h-10 rounded-full flex items-center justify-center text-white font-semibold text-sm',
            stock.market === 'KOSPI' || stock.market === 'KOSDAQ'
              ? 'bg-primary-500'
              : 'bg-blue-500'
          )}
        >
          {stock.name.charAt(0)}
        </div>
        <div>
          <p className="font-medium text-gray-900 dark:text-dark-text-primary">{stock.name}</p>
          {showQuantity && quantity ? (
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">{quantity}주 보유</p>
          ) : (
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">{stock.code}</p>
          )}
        </div>
      </div>

      <div className="flex items-center gap-4">
        <div className="text-right">
          <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
            {formatNumber(stock.currentPrice)}원
          </p>
          <p className={cn('text-sm', priceColor)}>
            {stock.changeRate > 0 ? '+' : ''}{formatNumber(stock.changePrice)} ({formatPercent(stock.changeRate)})
          </p>
        </div>
        
        {showHeart && (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onToggleFavorite?.();
            }}
            className="p-2 hover:bg-gray-100 dark:hover:bg-dark-bg-primary rounded-full transition-colors"
          >
            <Heart
              className={cn(
                'w-5 h-5',
                isFavorite
                  ? 'fill-danger-500 text-danger-500'
                  : 'text-gray-400'
              )}
            />
          </button>
        )}
      </div>
    </div>
  );
}
