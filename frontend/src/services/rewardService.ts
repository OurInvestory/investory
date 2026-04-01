import apiClient from './api'

export interface LevelInfo {
  level: number
  experience: number
  currentLevelExperience: number
  nextLevelExperience: number
  progress: number
}

export interface AchievementResponse {
  id: number
  code: string
  name: string
  description: string
  iconUrl?: string
  condition: string
  rewardExperience: number
  achieved: boolean
  progress: number
  maxProgress: number
  achievedAt?: string
}

export const rewardService = {
  getLevel: async () => {
    const response = await apiClient.get<{ data: LevelInfo }>('/rewards/level')
    return response.data.data
  },

  getAchievements: async () => {
    const response = await apiClient.get<{ data: AchievementResponse[] }>('/rewards/achievements')
    return response.data.data
  },
}
