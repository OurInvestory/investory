import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Lock, Trophy, Star, TrendingUp, Users, Target } from 'lucide-react';
import { Tabs, Card, ProgressBar, Badge } from '@/components/common';
import { cn, formatNumber } from '@/utils';
import { useLevelInfo, useAchievements } from '@/hooks/useApi';

const levelTiers = [
  { level: 1, title: '?낅Ц??, minExp: 0, color: 'bg-gray-400' },
  { level: 5, title: '珥덈낫 ?ъ옄??, minExp: 1000, color: 'bg-green-500' },
  { level: 10, title: '以묎툒 ?ъ옄??, minExp: 5000, color: 'bg-blue-500' },
  { level: 20, title: '?숇젴 ?ъ옄??, minExp: 15000, color: 'bg-purple-500' },
  { level: 30, title: '?꾨Ц ?ъ옄??, minExp: 35000, color: 'bg-yellow-500' },
  { level: 50, title: '留덉뒪??, minExp: 75000, color: 'bg-red-500' },
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
    title: levelTiers.find(t => levelData.level >= t.level)?.title || '?낅Ц??,
    currentExp: levelData.experience,
    requiredExp: levelData.nextLevelExperience || 1000,
    nextTitle: levelTiers.find(t => levelData.level < t.level)?.title || '留덉뒪??,
  } : {
    level: 1, title: '?낅Ц??, currentExp: 0, requiredExp: 1000, nextTitle: '珥덈낫 ?ъ옄??,
  };

  const achievements = (achievementsData || []).map((a: any) => ({
    id: a.id, name: a.name, description: a.description,
    icon: ACHIEVEMENT_ICONS[a.code] || Trophy,
    isUnlocked: a.achieved, unlockedAt: a.achievedAt,
    progress: a.progress, maxProgress: a.maxProgress, expReward: a.rewardExperience,
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
            {activeTab === 'level' ? '?덈꺼 & 寃쏀뿕移? : '?낆쟻'}
          </h1>
        </div>
      </div>

      <div className="px-4 py-4">
        {/* ??*/}
        <Tabs
          tabs={[
            { id: 'level', label: '?덈꺼 & 寃쏀뿕移? },
            { id: 'achievements', label: '?낆쟻' },
          ]}
          activeTab={activeTab}
          onChange={setActiveTab}
        />

        <div className="mt-4">
          {activeTab === 'level' && (
            <div className="space-y-4">
              {/* ?꾩옱 ?덈꺼 */}
              <Card className="p-6 text-center">
                <div className="w-20 h-20 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center mx-auto mb-4">
                  <span className="text-3xl font-bold text-white">Lv.{levelInfo.level}</span>
                </div>
                <h2 className="text-xl font-bold text-gray-900 dark:text-dark-text-primary mb-1">
                  {levelInfo.title}
                </h2>
                <p className="text-sm text-gray-500 dark:text-dark-text-secondary mb-4">
                  ?ㅼ쓬 ?덈꺼源뚯? {formatNumber(levelInfo.requiredExp - levelInfo.currentExp)} EXP ?꾩슂
                </p>

                <div className="mb-2">
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-500 dark:text-dark-text-secondary">寃쏀뿕移?/span>
                    <span className="text-primary-500 font-medium">
                      {formatNumber(levelInfo.currentExp)} / {formatNumber(levelInfo.requiredExp)}
                    </span>
                  </div>
                  <ProgressBar value={levelInfo.currentExp} max={levelInfo.requiredExp} color="primary" />
                </div>
              </Card>

              {/* ?덈꺼 ?곗뼱 */}
              <Card className="p-4">
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-4">?덈꺼 ?깃툒</h3>
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
                            Lv.{tier.level} ?ъ꽦 ??
                          </p>
                        </div>
                        {isCurrentTier && (
                          <Badge variant="primary">?꾩옱</Badge>
                        )}
                        {!isAchieved && (
                          <Lock className="w-5 h-5 text-gray-400" />
                        )}
                      </div>
                    );
                  })}
                </div>
              </Card>

              {/* 寃쏀뿕移??띾뱷 諛⑸쾿 */}
              <Card className="p-4">
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-4">寃쏀뿕移??띾뱷 諛⑸쾿</h3>
                <div className="space-y-3 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-dark-text-secondary">?쇱씪 濡쒓렇??/span>
                    <span className="text-primary-500 font-medium">+10 EXP</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-dark-text-secondary">二쇱떇 嫄곕옒 ?꾨즺</span>
                    <span className="text-primary-500 font-medium">+20 EXP</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-dark-text-secondary">寃뚯떆湲 ?묒꽦</span>
                    <span className="text-primary-500 font-medium">+15 EXP</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-dark-text-secondary">?낆쟻 ?ъ꽦</span>
                    <span className="text-primary-500 font-medium">+50~1000 EXP</span>
                  </div>
                </div>
              </Card>
            </div>
          )}

          {activeTab === 'achievements' && (
            <div className="space-y-4">
              {/* ?꾨즺???낆쟻 */}
              <div>
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-3">
                  ?ъ꽦???낆쟻 ({achievements.filter(a => a.isUnlocked).length})
                </h3>
                <div className="space-y-3">
                  {achievements.filter(a => a.isUnlocked).map((achievement) => {
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
                              ?ъ꽦?? {achievement.unlockedAt}
                            </p>
                          </div>
                          <div className="text-right">
                            <Badge variant="success">?꾨즺</Badge>
                            <p className="text-xs text-primary-500 mt-1">+{achievement.expReward} EXP</p>
                          </div>
                        </div>
                      </Card>
                    );
                  })}
                </div>
              </div>

              {/* 吏꾪뻾 以묒씤 ?낆쟻 */}
              <div>
                <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-3">
                  吏꾪뻾 以묒씤 ?낆쟻 ({achievements.filter(a => !a.isUnlocked).length})
                </h3>
                <div className="space-y-3">
                  {achievements.filter(a => !a.isUnlocked).map((achievement) => {
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
