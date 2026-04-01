import apiClient from './api'

export interface UserProfile {
  id: number
  loginId: string
  email: string
  nickname: string
  profileImageUrl?: string
  level: number
  experience: number
  wmtiType?: string
  createdAt: string
}

export interface UpdateProfileRequest {
  nickname?: string
  profileImageUrl?: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

export const userService = {
  getMe: async () => {
    const response = await apiClient.get<{ data: UserProfile }>('/users/me')
    return response.data.data
  },

  updateMe: async (data: UpdateProfileRequest) => {
    const response = await apiClient.patch<{ data: UserProfile }>('/users/me', data)
    return response.data.data
  },

  changePassword: async (data: ChangePasswordRequest) => {
    await apiClient.patch('/users/me/password', data)
  },

  deleteAccount: async () => {
    await apiClient.delete('/users/me')
  },

  getUser: async (userId: number) => {
    const response = await apiClient.get<{ data: UserProfile }>(`/users/${userId}`)
    return response.data.data
  },
}
