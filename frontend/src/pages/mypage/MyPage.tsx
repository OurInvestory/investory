import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, MoreHorizontal, ChevronRight, Heart, MessageCircle, Repeat2 } from 'lucide-react';
import { useAuthStore } from '@/stores/authStore';
import { Tabs, Card, Avatar, Badge } from '@/components/common';
import { cn, formatDate } from '@/utils';

// 목업 데이터
const mockUser = {
  name: '김투자',
  nickname: '김',
  joinDate: '2024.03.15',
  postsCount: 7,
  followingCount: 3,
  followersCount: 5,
  level: 5,
  experience: 1250,
  wmtiType: 'INTJ',
};

const mockActivities = [
  {
    id: 1,
    type: 'post',
    stockName: '테슬라',
    content: '이거 오르긴 하겠지?',
    likes: 12,
    comments: 5,
    date: '9월 9일',
  },
  {
    id: 2,
    type: 'post',
    stockName: '테슬라',
    content: '제발..',
    likes: 10,
    comments: 2,
    date: '9월 8일',
  },
  {
    id: 3,
    type: 'post',
    stockName: '테슬라',
    content: '드디어?',
    likes: 8,
    comments: 1,
    date: '9월 4일',
  },
  {
    id: 4,
    type: 'trade',
    action: 'buy',
    stockName: '테슬라',
    price: 23.50,
    shares: 1,
    total: 335.02,
    date: '9월 1일',
  },
  {
    id: 5,
    type: 'trade',
    action: 'sell',
    stockName: '테슬라',
    price: 1.41,
    shares: 1,
    total: 329.45,
    date: '9월 1일',
  },
];

export default function MyPage() {
  const navigate = useNavigate();
  const { user: authUser, logout } = useAuthStore();
  const [activeTab, setActiveTab] = useState('activity');
  const [activityFilter, setActivityFilter] = useState('all');

  const user = { ...mockUser, ...authUser };

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
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">{mockUser.postsCount}</p>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">작성글</p>
          </div>
          <button className="text-center">
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
              {mockUser.followingCount} <ChevronRight className="w-4 h-4 inline" />
            </p>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">팔로워</p>
          </button>
          <button className="text-center">
            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
              {mockUser.followersCount} <ChevronRight className="w-4 h-4 inline" />
            </p>
            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">팔로잉</p>
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
                {mockActivities
                  .filter((a) => activityFilter === 'all' || 
                    (activityFilter === 'posts' && a.type === 'post') ||
                    (activityFilter === 'trades' && a.type === 'trade'))
                  .map((activity) => (
                    <Card key={activity.id} className="p-4">
                      <div className="flex items-start justify-between mb-2">
                        <div className="flex items-center gap-2">
                          <Badge variant={activity.type === 'post' ? 'primary' : 'success'}>
                            {activity.stockName}
                          </Badge>
                          <span className="text-sm text-gray-500 dark:text-dark-text-secondary">
                            {activity.type === 'post' ? '에서 작성한 글' : 
                              activity.action === 'buy' ? '1주 구매' : '1주 판매'}
                          </span>
                        </div>
                        <button className="p-1">
                          <MoreHorizontal className="w-4 h-4 text-gray-400" />
                        </button>
                      </div>

                      {activity.type === 'post' ? (
                        <>
                          <p className="text-gray-900 dark:text-dark-text-primary mb-3">
                            {activity.content}
                          </p>
                          <div className="flex items-center gap-4 text-sm text-gray-500 dark:text-dark-text-secondary">
                            <button className="flex items-center gap-1">
                              <Heart className="w-4 h-4" />
                              {activity.likes}
                            </button>
                            <button className="flex items-center gap-1">
                              <MessageCircle className="w-4 h-4" />
                              {activity.comments}
                            </button>
                            <button className="flex items-center gap-1">
                              <Repeat2 className="w-4 h-4" />
                            </button>
                          </div>
                        </>
                      ) : (
                        <div className="flex justify-between text-sm">
                          <span className="text-gray-500 dark:text-dark-text-secondary">
                            ${activity.price?.toFixed(2)} 1주당 ${activity.total?.toFixed(2)}
                          </span>
                        </div>
                      )}

                      <p className="text-xs text-gray-400 mt-2">{activity.date}</p>
                    </Card>
                  ))}
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
