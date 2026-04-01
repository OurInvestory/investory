import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Search as SearchIcon, X, TrendingUp } from 'lucide-react';
import { Input, Badge, StockListItem } from '@/components/common';
import { useStockSearch, useTopStocks } from '@/hooks/useApi';
import type { Stock } from '@/types';

const recentSearches = ['삼성전자', '테슬라', '애플'];

const categoryButtons = [
  { icon: '🏢', label: '대형주', color: 'bg-blue-100' },
  { icon: '📈', label: '테마주', color: 'bg-green-100' },
  { icon: '💰', label: '금융주', color: 'bg-yellow-100' },
  { icon: '🌱', label: '신규상장', color: 'bg-purple-100' },
];

export default function SearchPage() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');

  // Real API hooks
  const { data: searchData } = useStockSearch(searchQuery);
  const { data: topStocksData } = useTopStocks('volume', 10);

  const searchResults: Stock[] = searchData?.content
    ? searchData.content.map((s: any) => ({
        id: s.id,
        code: s.code,
        name: s.name,
        market: s.market,
        currentPrice: s.currentPrice,
        previousClose: s.currentPrice - s.changeAmount,
        changePrice: s.changeAmount,
        changeRate: s.changeRate,
        volume: s.volume,
      }))
    : [];

  const topStocks = topStocksData
    ? topStocksData.map((s: any, idx: number) => ({
        rank: idx + 1,
        name: s.name,
        change: s.changeRate,
      }))
    : [];

  const handleSearch = (query: string) => {
    setSearchQuery(query);
  };

  return (
    <div className="min-h-screen bg-white dark:bg-dark-bg-primary">
      {/* Header */}
      <div className="sticky top-0 bg-white dark:bg-dark-bg-primary z-10 border-b border-gray-100 dark:border-dark-border">
        <div className="flex items-center gap-3 px-4 h-14">
          <button onClick={() => navigate(-1)} className="p-1">
            <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
          </button>
          <div className="flex-1 relative">
            <Input
              placeholder="검색 (종목 및 주식 검색하기에서)"
              value={searchQuery}
              onChange={(e) => handleSearch(e.target.value)}
              leftIcon={<SearchIcon className="w-5 h-5" />}
              rightIcon={
                searchQuery && (
                  <button onClick={() => handleSearch('')}>
                    <X className="w-5 h-5" />
                  </button>
                )
              }
              className="py-2"
            />
          </div>
        </div>

        {/* 최근 검색어 */}
        {!searchQuery && recentSearches.length > 0 && (
          <div className="flex items-center gap-2 px-4 py-2 overflow-x-auto">
            {recentSearches.map((term) => (
              <button
                key={term}
                onClick={() => handleSearch(term)}
                className="flex items-center gap-1 px-3 py-1.5 bg-gray-100 dark:bg-dark-bg-secondary rounded-full text-sm whitespace-nowrap"
              >
                {term}
                <X className="w-3 h-3 text-gray-400" />
              </button>
            ))}
            <button className="text-sm text-primary-500 whitespace-nowrap">
              전체삭제 ×
            </button>
          </div>
        )}
      </div>

      <div className="px-4 py-4">
        {searchQuery ? (
          // 검색 결과
          <div className="space-y-4">
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">
              검색 결과 {searchResults.length}건
            </p>
            {searchResults.length > 0 ? (
              <div className="space-y-2">
                {searchResults.map((stock) => (
                  <StockListItem
                    key={stock.id}
                    stock={stock}
                    onClick={() => navigate(`/stock/${stock.code}`)}
                  />
                ))}
              </div>
            ) : (
              <div className="text-center py-10 text-gray-500 dark:text-dark-text-secondary">
                검색 결과가 없습니다.
              </div>
            )}
          </div>
        ) : (
          // 초기 화면
          <div className="space-y-6">
            {/* 인기 검색어 */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <h2 className="font-semibold text-gray-900 dark:text-dark-text-primary">인기 검색어</h2>
                <span className="text-xs text-gray-500 dark:text-dark-text-secondary">오늘 16:00 기준</span>
              </div>
              <div className="space-y-1">
                {(topStocks.length > 0 ? topStocks : []).map((item: any) => (
                  <button
                    key={item.rank}
                    onClick={() => handleSearch(item.name)}
                    className="w-full flex items-center justify-between py-2.5 px-1 hover:bg-gray-50 dark:hover:bg-dark-bg-secondary rounded-lg transition-colors"
                  >
                    <div className="flex items-center gap-3">
                      <span className="w-6 text-center font-semibold text-gray-700 dark:text-dark-text-primary">
                        {item.rank}
                      </span>
                      <span className="text-gray-900 dark:text-dark-text-primary">{item.name}</span>
                    </div>
                    <span className={item.change >= 0 ? 'text-danger-500' : 'text-success-500'}>
                      {item.change >= 0 ? '+' : ''}{item.change}%
                    </span>
                  </button>
                ))}
              </div>
            </div>

            {/* 카테고리별 검색 */}
            <div>
              <h2 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-3">
                카테고리별 검색
              </h2>
              <div className="grid grid-cols-4 gap-3">
                {categoryButtons.map((cat) => (
                  <button
                    key={cat.label}
                    className="flex flex-col items-center gap-2 p-3 rounded-xl hover:bg-gray-50 dark:hover:bg-dark-bg-secondary transition-colors"
                  >
                    <div className={`w-12 h-12 ${cat.color} rounded-full flex items-center justify-center text-2xl`}>
                      {cat.icon}
                    </div>
                    <span className="text-xs text-gray-600 dark:text-dark-text-secondary">{cat.label}</span>
                  </button>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
