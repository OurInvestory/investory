import apiClient from './api'

export interface LevelInfo {
  level: number
  title: string
  currentExp: number
  requiredExp: number
  expToNextLevel: number
  progressPercent: number
  tiers: { level: number; title: string; minExp: number }[]
}

export interface AchievementItem {
  id: number
  code: string
  name: string
  description: string
  icon: string
  category: string
  expReward: number
  maxProgress: number
  currentProgress: number
  isUnlocked: boolean
  unlockedAt?: string
}

export interface AchievementSummary {
  totalCount: number
  unlockedCount: number
  totalExpEarned: number
  achievements: AchievementItem[]
}

export const rewardService = {
  getLevel: async () => {
    const response = await apiClient.get<{ data: LevelInfo }>('/rewards/level')
    return response.data.data
  },

  getAchievements: async (): Promise<AchievementItem[]> => {
    const response = await apiClient.get<{ data: AchievementSummary }>('/rewards/achievements')
    return response.data.data.achievements
  },
}
