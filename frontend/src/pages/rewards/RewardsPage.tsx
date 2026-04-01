import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Lock, Trophy, Star, TrendingUp, Users, Target } from 'lucide-react';
import { Tabs, Card, ProgressBar, Badge } from '@/components/common';
import { cn, formatNumber } from '@/utils';
import { useLevelInfo, useAchievements } from '@/hooks/useApi';

const levelTiers = [
  { level: 1, title: '입문자', minExp: 0, color: 'bg-gray-400' },
  { level: 5, title: '초보 투자자', minExp: 1000, color: 'bg-green-500' },
  { level: 10, title: '중급 투자자', minExp: 5000, color: 'bg-blue-500' },
  { level: 20, title: '숙련 투자자', minExp: 15000, color: 'bg-purple-500' },
  { level: 30, title: '전문 투자자', minExp: 35000, color: 'bg-yellow-500' },
  { level: 50, title: '마스터', minExp: 75000, color: 'bg-red-500' },
];

const ACHIEVEMENT_ICONS: Record<string, any> = {
  FIRST_TRADE: Target,
  PROFIT_10: TrendingUp,
  COMMUNITY: Users,
  DIVERSIFY: Star,
  STREAK_30: Trophy,
  PROFIT_KING: Trophy,
};

export default function RewardsPage() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('level');

  const { data: levelData } = useLevelInfo();
  const { data: achievementsData } = useAchievements();

  const levelInfo = levelData ? {
    level: levelData.level,
    title: levelData.title || levelTiers.find(t => levelData.level >= t.level)?.title || '입문자',
    currentExp: levelData.currentExp,
    requiredExp: levelData.requiredExp || 1000,
    nextTitle: levelTiers.find(t => levelData.level < t.level)?.title || '마스터',
  } : {
    level: 1, title: '입문자', currentExp: 0, requiredExp: 1000, nextTitle: '초보 투자자',
  };

  const achievements = (achievementsData || []).map((a: any) => ({
    id: a.id, name: a.name, description: a.description, code: a.code,
    icon: ACHIEVEMENT_ICONS[a.code] || Trophy,
    isUnlocked: a.isUnlocked, unlockedAt: a.unlockedAt,
    progress: a.currentProgress, maxProgress: a.maxProgress, expReward: a.expReward,
  }));

  const expProgress = levelInfo.requiredExp > 0 ? (levelInfo.currentExp / levelInfo.requiredExp) * 100 : 0;

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-dark-bg-primary pb-20">
      {/* Header */}
      <div className="sticky top-0 bg-white dark:bg-dark-bg-secondary z-10 border-b border-gray-100 dark:border-dark-border">
        <div className="flex items-center px-4 h-14">
          <button onClick={() => navigate(-1)} className="p-2 -ml-2">
            <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
          </button>
          <h1 className="font-semibold text-gray-900 dark:text-dark-text-primary ml-2">
            {activeTab === 'level' ? '레벨 & 경험치' : '업적'}
          </h1>
        </div>
      </div>

      <div className="px-4 py-4">
        {/* 탭 */}
        <Tabs
          tabs={[
            { id: 'level', label: '레벨 & 경험치' },
            { id: 'achievements', label: '업적' },
          ]}
          activeTab={activeTab}
          onChange={setActiveTab}
        />

        <div className="mt-4">
          {activeTab === 'level' && (
            <div className="space-y-4">
              {/* 현재 레벨 */}
              <Card className="p-6 text-center">
                <div className="w-20 h-20 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center mx-auto mb-4">
                  <span className="text-3xl font-bold text-white">Lv.{levelInfo.level}</span>
                </div>
                <h2 className="text-xl font-bold text-gray-900 dark:text-dark-text-primary mb-1">
                  {levelInfo.title}
                </h2>
                <p className="text-sm text-gray-500 dark:text-dark-text-secondary mb-4">
                  다음 레벨까지 {formatNumber(levelInfo.requiredExp - levelInfo.currentExp)} EXP 필요
                </p>

                <div className="mb-2">
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-500 dark:text-dark-text-secondary">경험치</span>
                    <span className="text-primary-500 font-medium">
                      {formatNumber(levelInfo.currentExp)} / {formatNumber(levelInfo.requiredExp)}
                    </span>
                  </div>
                  <ProgressBar value={levelInfo.currentExp} max={levelInfo.requiredExp} color="primary" />
                </div>
              </Card>

              {/* 레벨 티어 */}
              <Card className="p-4">
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-4">레벨 시스템</h3>
                <div className="space-y-3">
                  {levelTiers.map((tier) => {
                    const isCurrentTier = levelInfo.level >= tier.level && 
                      (levelTiers.indexOf(tier) === levelTiers.length - 1 || 
                       levelInfo.level < levelTiers[levelTiers.indexOf(tier) + 1].level);
                    const isAchieved = levelInfo.level >= tier.level;

                    return (
                      <div
                        key={tier.level}
                        className={cn(
                          'flex items-center gap-3 p-3 rounded-xl',
                          isCurrentTier && 'bg-primary-50 dark:bg-primary-900/20 ring-2 ring-primary-500'
                        )}
                      >
                        <div
                          className={cn(
                            'w-10 h-10 rounded-full flex items-center justify-center text-white font-semibold',
                            isAchieved ? tier.color : 'bg-gray-300 dark:bg-dark-border'
                          )}
                        >
                          {tier.level}
                        </div>
                        <div className="flex-1">
                          <p className={cn(
                            'font-medium',
                            isAchieved 
                              ? 'text-gray-900 dark:text-dark-text-primary' 
                              : 'text-gray-400 dark:text-dark-text-secondary'
                          )}>
                            {tier.title}
                          </p>
                          <p className="text-sm text-gray-500 dark:text-dark-text-secondary">
                            Lv.{tier.level} 달성 시
                          </p>
                        </div>
                        {isCurrentTier && (
                          <Badge variant="primary">현재</Badge>
                        )}
                        {!isAchieved && (
                          <Lock className="w-5 h-5 text-gray-400" />
                        )}
                      </div>
                    );
                  })}
                </div>
              </Card>

              {/* 경험치 획득 방법 */}
              <Card className="p-4">
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-4">경험치 획득 방법</h3>
                <div className="space-y-3 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-dark-text-secondary">일일 로그인</span>
                    <span className="text-primary-500 font-medium">+10 EXP</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-dark-text-secondary">주식 거래 완료</span>
                    <span className="text-primary-500 font-medium">+20 EXP</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-dark-text-secondary">게시글 작성</span>
                    <span className="text-primary-500 font-medium">+15 EXP</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-dark-text-secondary">업적 달성</span>
                    <span className="text-primary-500 font-medium">+50~1000 EXP</span>
                  </div>
                </div>
              </Card>
            </div>
          )}

          {activeTab === 'achievements' && (
            <div className="space-y-4">
              {/* 달성한 업적 */}
              <div>
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-3">
                  달성한 업적 ({achievements.filter((a: any) => a.isUnlocked).length})
                </h3>
                <div className="space-y-3">
                  {achievements.filter((a: any) => a.isUnlocked).map((achievement: any) => {
                    const Icon = achievement.icon;
                    return (
                      <Card key={achievement.id} className="p-4">
                        <div className="flex items-center gap-3">
                          <div className="w-12 h-12 bg-primary-100 dark:bg-primary-900/30 rounded-full flex items-center justify-center">
                            <Icon className="w-6 h-6 text-primary-500" />
                          </div>
                          <div className="flex-1">
                            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
                              {achievement.name}
                            </p>
                            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">
                              {achievement.description}
                            </p>
                            <p className="text-xs text-gray-400 dark:text-dark-text-secondary mt-1">
                              달성일: {achievement.unlockedAt}
                            </p>
                          </div>
                          <div className="text-right">
                            <Badge variant="success">완료</Badge>
                            <p className="text-xs text-primary-500 mt-1">+{achievement.expReward} EXP</p>
                          </div>
                        </div>
                      </Card>
                    );
                  })}
                </div>
              </div>

              {/* 진행 중인 업적 */}
              <div>
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-3">
                  진행 중인 업적 ({achievements.filter((a: any) => !a.isUnlocked).length})
                </h3>
                <div className="space-y-3">
                  {achievements.filter((a: any) => !a.isUnlocked).map((achievement: any) => {
                    const Icon = achievement.icon;
                    return (
                      <Card key={achievement.id} className="p-4 opacity-80">
                        <div className="flex items-center gap-3">
                          <div className="w-12 h-12 bg-gray-100 dark:bg-dark-bg-secondary rounded-full flex items-center justify-center">
                            <Icon className="w-6 h-6 text-gray-400" />
                          </div>
                          <div className="flex-1">
                            <p className="font-semibold text-gray-900 dark:text-dark-text-primary">
                              {achievement.name}
                            </p>
                            <p className="text-sm text-gray-500 dark:text-dark-text-secondary">
                              {achievement.description}
                            </p>
                            {achievement.progress !== undefined && (
                              <div className="mt-2">
                                <ProgressBar 
                                  value={achievement.progress} 
                                  max={achievement.maxProgress || 100} 
                                />
                                <p className="text-xs text-gray-400 mt-1">
                                  {achievement.progress}/{achievement.maxProgress}
                                </p>
                              </div>
                            )}
                          </div>
                          <div className="text-right">
                            <Lock className="w-5 h-5 text-gray-400 mx-auto" />
                            <p className="text-xs text-gray-400 mt-1">+{achievement.expReward} EXP</p>
                          </div>
                        </div>
                      </Card>
                    );
                  })}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}