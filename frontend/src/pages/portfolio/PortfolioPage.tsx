import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, MoreHorizontal, ChevronRight } from 'lucide-react';
import { Tabs, Card, Button, Badge } from '@/components/common';
import { cn, formatNumber, formatPercent } from '@/utils';
import { usePortfolio } from '@/hooks/useApi';
import { Doughnut } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';

ChartJS.register(ArcElement, Tooltip, Legend);

const CHART_COLORS = ['#FF6384', '#36A2EB', '#4BC0C0', '#FFCE56', '#9966FF', '#FF9F40', '#C9CBCF', '#7BC8A4'];

export default function PortfolioPage() {
  const navigate = useNavigate();
  const [showChart, setShowChart] = useState(false);
  const [sortBy, setSortBy] = useState<'return' | 'value'>('return');

  const { data: portfolioData, isLoading } = usePortfolio();

  const portfolio = portfolioData ? {
    totalValue: portfolioData.totalValue,
    totalInvested: portfolioData.totalInvestment,
    totalReturn: portfolioData.totalProfitLoss,
    totalReturnRate: portfolioData.totalProfitLossRate,
    dailyReturn: portfolioData.dailyProfitLoss,
    dailyReturnRate: portfolioData.dailyProfitLossRate,
  } : {
    totalValue: 0, totalInvested: 0, totalReturn: 0, totalReturnRate: 0, dailyReturn: 0, dailyReturnRate: 0,
  };

  const holdings = (portfolioData?.holdings || []).map((h: any, i: number) => ({
    id: h.id, name: h.stockName, code: h.stockCode, market: h.market,
    quantity: h.quantity, avgPrice: h.averagePrice, currentPrice: h.currentPrice,
    totalValue: h.totalValue, returnRate: h.profitLossRate, returnAmount: h.profitLoss,
    color: CHART_COLORS[i % CHART_COLORS.length],
  }));

  const domesticHoldings = holdings.filter((h: any) => ['KOSPI', 'KOSDAQ'].includes(h.market));
  const foreignHoldings = holdings.filter((h: any) => ['NASDAQ', 'NYSE', 'AMEX'].includes(h.market));

  // 차트 데이터
  const chartData = {
    labels: holdings.map((h: any) => h.name),
    datasets: [
      {
        data: holdings.map((h: any) => h.totalValue),
        backgroundColor: holdings.map((h: any) => h.color),
        borderWidth: 0,
      },
    ],
  };

  const chartOptions = {
    cutout: '70%',
    plugins: {
      legend: {
        display: false,
      },
    },
  };

  const priceColor = portfolio.totalReturnRate >= 0 ? 'text-danger-500' : 'text-success-500';
  const dailyColor = portfolio.dailyReturnRate >= 0 ? 'text-danger-500' : 'text-success-500';

  if (isLoading) {
    return (
      <div className="min-h-screen bg-white dark:bg-dark-bg-primary flex items-center justify-center">
        <p className="text-gray-500">로딩 중...</p>
      </div>
    );
  }

  if (showChart) {
    return (
      <div className="min-h-screen bg-white dark:bg-dark-bg-primary">
        {/* Header */}
        <div className="sticky top-0 bg-white dark:bg-dark-bg-primary z-10 border-b border-gray-100 dark:border-dark-border">
          <div className="flex items-center px-4 h-14">
            <button onClick={() => setShowChart(false)} className="p-2 -ml-2">
              <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
            </button>
          </div>
        </div>

        <div className="px-4 py-6">
          {/* 총 금액 */}
          <div className="text-center mb-8">
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary mb-1">총 금액</p>
            <p className="text-3xl font-bold text-gray-900 dark:text-dark-text-primary">
              {formatNumber(portfolio.totalValue)}원
            </p>
          </div>

          {/* 도넛 차트 */}
          <div className="relative w-64 h-64 mx-auto mb-8">
            <Doughnut data={chartData} options={chartOptions} />
            <div className="absolute inset-0 flex flex-col items-center justify-center">
              <p className="text-sm text-gray-500 dark:text-dark-text-secondary">총 수익률</p>
              <p className={cn('text-2xl font-bold', priceColor)}>
                {portfolio.totalReturnRate >= 0 ? '+' : ''}{portfolio.totalReturnRate.toFixed(1)}%
              </p>
            </div>
          </div>

          {/* 종목별 비중 */}
          <div className="space-y-4">
            {holdings.map((holding: any) => {
              const percentage = portfolio.totalValue > 0 ? ((holding.totalValue / portfolio.totalValue) * 100).toFixed(0) : '0';
              return (
                <div key={holding.id} className="flex items-center gap-3">
                  <div
                    className="w-3 h-3 rounded-full"
                    style={{ backgroundColor: holding.color }}
                  />
                  <div className="flex-1">
                    <p className="font-medium text-gray-900 dark:text-dark-text-primary">{holding.name}</p>
                    <p className="text-sm text-gray-500 dark:text-dark-text-secondary">
                      {formatNumber(holding.totalValue)}원
                    </p>
                  </div>
                  <span className="text-lg font-semibold text-gray-700 dark:text-dark-text-primary">
                    {percentage}%
                  </span>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white dark:bg-dark-bg-primary">
      {/* Header */}
      <div className="sticky top-0 bg-white dark:bg-dark-bg-primary z-10 border-b border-gray-100 dark:border-dark-border">
        <div className="flex items-center justify-between px-4 h-14">
          <button onClick={() => navigate(-1)} className="p-2 -ml-2">
            <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
          </button>
          <button className="p-2">
            <MoreHorizontal className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
          </button>
        </div>
      </div>

      <div className="px-4 py-4">
        {/* 총 자산 */}
        <div className="mb-6">
          <p className="text-sm text-gray-500 dark:text-dark-text-secondary mb-1">내 투자 금액</p>
          <p className="text-3xl font-bold text-gray-900 dark:text-dark-text-primary mb-1">
            {formatNumber(portfolio.totalValue)}원
          </p>
          <p className="text-sm text-gray-500 dark:text-dark-text-secondary flex items-center gap-1">
            <span className="inline-block w-4 h-4 rounded-full border border-gray-300 text-center text-xs">i</span>
            예상 수수료 세금 포함
          </p>
        </div>

        {/* 수익 정보 */}
        <div className="grid grid-cols-2 gap-4 mb-6">
          <div>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">원금</p>
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
              {formatNumber(portfolio.totalInvested)}원
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">총 수익</p>
            <p className={cn('font-semibold', priceColor)}>
              {portfolio.totalReturn >= 0 ? '+' : ''}{formatNumber(portfolio.totalReturn)}원 ({formatPercent(portfolio.totalReturnRate)})
            </p>
          </div>
          <div className="col-span-2">
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">일간 수익</p>
            <p className={cn('font-semibold', dailyColor)}>
              {portfolio.dailyReturn >= 0 ? '+' : ''}{formatNumber(portfolio.dailyReturn)}원 ({formatPercent(portfolio.dailyReturnRate)})
            </p>
          </div>
        </div>

        {/* 투자 비중 보기 */}
        <button
          onClick={() => setShowChart(true)}
          className="w-full flex items-center justify-between py-3 px-4 bg-danger-50 dark:bg-danger-900/20 rounded-xl mb-6"
        >
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-danger-500" />
            <span className="font-medium text-gray-900 dark:text-dark-text-primary">투자 비중 보기</span>
          </div>
          <ChevronRight className="w-5 h-5 text-gray-400" />
        </button>

        {/* 국내주식 */}
        <div className="mb-6">
          <div className="flex items-center justify-between mb-3">
            <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary">국내주식</h3>
            <button
              onClick={() => setSortBy(sortBy === 'return' ? 'value' : 'return')}
              className="text-sm text-primary-500"
            >
              {sortBy === 'return' ? '수익률순 ▼' : '평가금액순 ▼'}
            </button>
          </div>

          <Card className="divide-y divide-gray-100 dark:divide-dark-border">
            {domesticHoldings.map((holding: any) => (
              <div
                key={holding.id}
                onClick={() => navigate(`/stock/${holding.code}`)}
                className="p-4 hover:bg-gray-50 dark:hover:bg-dark-bg-primary cursor-pointer"
              >
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-primary-500 rounded-full flex items-center justify-center text-white font-semibold">
                      {holding.name.charAt(0)}
                    </div>
                    <div>
                      <p className="font-medium text-gray-900 dark:text-dark-text-primary">{holding.name}</p>
                      <p className="text-sm text-gray-500 dark:text-dark-text-secondary">{holding.quantity}주 보유</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
                      {formatNumber(holding.totalValue)}원
                    </p>
                    <p className={cn(
                      'text-sm',
                      holding.returnRate >= 0 ? 'text-danger-500' : 'text-success-500'
                    )}>
                      {holding.returnRate >= 0 ? '+' : ''}{formatNumber(holding.returnAmount)}원 ({formatPercent(holding.returnRate)})
                    </p>
                  </div>
                </div>
                <div className="flex gap-2 text-xs text-gray-500 dark:text-dark-text-secondary">
                  <span>평균 매수가: {formatNumber(holding.avgPrice)}원</span>
                  <span>•</span>
                  <span>현재가: {formatNumber(holding.currentPrice)}원</span>
                </div>
                <div className="flex gap-2 mt-3">
                  <Button variant="outline" size="sm" className="flex-1">
                    판매하기
                  </Button>
                  <Button variant="primary" size="sm" className="flex-1">
                    구매하기
                  </Button>
                </div>
              </div>
            ))}
          </Card>
        </div>

        {/* 해외주식 */}
        <div>
          <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-3">해외주식</h3>
          <Card className="p-4">
            <div className="flex items-center justify-between text-sm text-gray-500 dark:text-dark-text-secondary border-b border-gray-100 dark:border-dark-border pb-2 mb-3">
              <span>종목명</span>
              <div className="flex gap-6">
                <span>총수익</span>
                <span>1주 평균금액</span>
                <span>총 금액</span>
              </div>
            </div>
            {foreignHoldings.map((holding: any) => (
              <div
                key={holding.id}
                className="flex items-center justify-between py-2"
              >
                <span className="text-gray-900 dark:text-dark-text-primary">{holding.name}</span>
                <div className="flex gap-6 text-sm">
                  <span className={holding.returnRate >= 0 ? 'text-danger-500' : 'text-success-500'}>
                    {formatPercent(holding.returnRate)}
                  </span>
                  <span className="text-gray-700 dark:text-dark-text-primary">{formatNumber(holding.avgPrice)}원</span>
                  <span className="text-gray-700 dark:text-dark-text-primary">{formatNumber(holding.totalValue)}원</span>
                </div>
              </div>
            ))}
          </Card>
        </div>
      </div>
    </div>
  );
}
