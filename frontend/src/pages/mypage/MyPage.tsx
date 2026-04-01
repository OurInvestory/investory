import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, MoreHorizontal, ChevronRight } from 'lucide-react';
import { useAuthStore } from '@/stores/authStore';
import { Tabs, Card, Avatar, Badge } from '@/components/common';
import { cn, formatDate, formatNumber } from '@/utils';
import { useMyProfile, useOrders } from '@/hooks/useApi';

export default function MyPage() {
  const navigate = useNavigate();
  const { user: authUser, logout } = useAuthStore();
  const [activeTab, setActiveTab] = useState('activity');
  const [activityFilter, setActivityFilter] = useState('all');

  const { data: profileData } = useMyProfile();
  const { data: ordersData } = useOrders(0, 10);

  const user = profileData ? {
    name: profileData.nickname,
    nickname: profileData.nickname,
    joinDate: profileData.createdAt ? formatDate(profileData.createdAt, 'long') : '',
    level: profileData.level,
    experience: profileData.experience,
    wmtiType: profileData.wmtiType,
  } : {
    name: authUser?.nickname || '사용자',
    nickname: authUser?.nickname || '사용자',
    joinDate: '',
    level: 1,
    experience: 0,
    wmtiType: undefined,
  };

  const recentOrders = (ordersData?.content || []).map((o: any) => ({
    id: o.id,
    type: 'trade' as const,
    action: o.type === 'BUY' ? 'buy' : 'sell',
    stockName: o.stockName || o.stockCode,
    price: o.filledPrice || o.price,
    shares: o.quantity,
    total: o.totalAmount,
    date: o.createdAt ? formatDate(o.createdAt, 'relative') : '',
  }));

  return (
    <div className="min-h-screen bg-white dark:bg-dark-bg-primary pb-20">
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

      <div className="px-4 py-6">
        {/* 프로필 */}
        <div className="flex items-center gap-4 mb-6">
          <Avatar name={user.name || user.nickname || '사용자'} size="xl" />
          <div className="flex-1">
            <h1 className="text-xl font-bold text-gray-900 dark:text-dark-text-primary">
              {user.name || user.nickname}
            </h1>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">
              가입일: {user.joinDate || formatDate(new Date().toISOString())}
            </p>
          </div>
        </div>

        {/* 통계 */}
        <div className="flex justify-around py-4 border-y border-gray-100 dark:border-dark-border mb-6">
          <div className="text-center">
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">Lv.{user.level}</p>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">레벨</p>
          </div>
          <div className="text-center">
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">{user.experience}</p>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">경험치</p>
          </div>
          <button className="text-center" onClick={() => navigate('/wmti')}>
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
              {user.wmtiType || '미진단'} <ChevronRight className="w-4 h-4 inline" />
            </p>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">WMTI</p>
          </button>
        </div>

        {/* 편집하기 */}
        <button
          onClick={() => navigate('/settings')}
          className="w-full flex items-center justify-between py-3 mb-6"
        >
          <span className="font-medium text-gray-900 dark:text-dark-text-primary">편집하기</span>
          <ChevronRight className="w-5 h-5 text-gray-400" />
        </button>

        {/* 탭 */}
        <Tabs
          tabs={[
            { id: 'activity', label: '최근 활동' },
            { id: 'investment', label: '투자' },
            { id: 'achievement', label: '업적' },
          ]}
          activeTab={activeTab}
          onChange={setActiveTab}
        />

        {/* 탭 컨텐츠 */}
        <div className="mt-4">
          {activeTab === 'activity' && (
            <div>
              {/* 필터 */}
              <div className="flex gap-2 mb-4">
                {[
                  { id: 'all', label: '전체' },
                  { id: 'posts', label: '작성글' },
                  { id: 'trades', label: '거래' },
                ].map((filter) => (
                  <button
                    key={filter.id}
                    onClick={() => setActivityFilter(filter.id)}
                    className={cn(
                      'px-4 py-1.5 rounded-full text-sm font-medium transition-colors',
                      activityFilter === filter.id
                        ? 'bg-primary-500 text-white'
                        : 'bg-gray-100 dark:bg-dark-bg-secondary text-gray-600 dark:text-dark-text-secondary'
                    )}
                  >
                    {filter.label}
                  </button>
                ))}
              </div>

              {/* 활동 목록 */}
              <div className="space-y-4">
                {recentOrders.length > 0 ? recentOrders
                  .filter((a: any) => activityFilter === 'all' || activityFilter === 'trades')
                  .map((activity: any) => (
                    <Card key={activity.id} className="p-4">
                      <div className="flex items-start justify-between mb-2">
                        <div className="flex items-center gap-2">
                          <Badge variant={activity.action === 'buy' ? 'primary' : 'success'}>
                            {activity.stockName}
                          </Badge>
                          <span className="text-sm text-gray-500 dark:text-dark-text-secondary">
                            {activity.action === 'buy' ? `${activity.shares}주 구매` : `${activity.shares}주 판매`}
                          </span>
                        </div>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-500 dark:text-dark-text-secondary">
                          {formatNumber(activity.price)}원 × {activity.shares}주 = {formatNumber(activity.total)}원
                        </span>
                      </div>
                      <p className="text-xs text-gray-400 mt-2">{activity.date}</p>
                    </Card>
                  )) : (
                  <div className="text-center py-10 text-gray-500 dark:text-dark-text-secondary">
                    최근 활동이 없습니다.
                  </div>
                )}
              </div>
            </div>
          )}

          {activeTab === 'investment' && (
            <div className="text-center py-10 text-gray-500 dark:text-dark-text-secondary">
              투자 내역이 없습니다.
            </div>
          )}

          {activeTab === 'achievement' && (
            <div className="text-center py-10 text-gray-500 dark:text-dark-text-secondary">
              달성한 업적이 없습니다.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
