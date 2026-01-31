import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Search as SearchIcon, X, TrendingUp } from 'lucide-react';
import { Input, Badge, StockListItem } from '@/components/common';
import type { Stock } from '@/types';

// 목업 데이터 - 인기 검색어
const popularSearches = [
  { rank: 1, name: '삼성전자', change: 2.34 },
  { rank: 2, name: 'SK하이닉스', change: -1.56 },
  { rank: 3, name: 'NAVER', change: 1.83 },
  { rank: 4, name: 'LG에너지솔루션', change: -1.04 },
  { rank: 5, name: '두산에너빌리티', change: 2.3 },
  { rank: 6, name: '삼성바이오', change: 2.41 },
  { rank: 7, name: '로봇트즈', change: -4.47 },
  { rank: 8, name: '카카오', change: 3.3 },
  { rank: 9, name: '현대차', change: 3.3 },
  { rank: 10, name: 'HJ중공업', change: 5.3 },
];

const recentSearches = ['삼성전자', '테슬라', '애플'];

const categoryButtons = [
  { icon: '🏢', label: '대형주', color: 'bg-blue-100' },
  { icon: '📈', label: '테마주', color: 'bg-green-100' },
  { icon: '💰', label: '금융주', color: 'bg-yellow-100' },
  { icon: '🌱', label: '신규상장', color: 'bg-purple-100' },
];

const mockSearchResults: Stock[] = [
  { id: 1, code: '005930', name: '삼성전자', market: 'KOSPI', currentPrice: 71500, previousClose: 70000, changePrice: 1500, changeRate: 2.14, volume: 15234567 },
  { id: 2, code: '005935', name: '삼성전자우', market: 'KOSPI', currentPrice: 58000, previousClose: 57500, changePrice: 500, changeRate: 0.87, volume: 5234567 },
];

export default function SearchPage() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Stock[]>([]);

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    if (query.length > 0) {
      // 실제로는 API 호출
      setSearchResults(mockSearchResults.filter(s => 
        s.name.toLowerCase().includes(query.toLowerCase()) ||
        s.code.toLowerCase().includes(query.toLowerCase())
      ));
    } else {
      setSearchResults([]);
    }
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
                {popularSearches.map((item) => (
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
