import apiClient from './api'

export interface LoginRequest {
  loginId: string
  password: string
}

export interface RegisterRequest {
  loginId: string
  email: string
  password: string
  nickname: string
  phone?: string
  gender?: string
  birthYear?: number
}

export interface AuthUser {
  id: number
  loginId: string
  email: string
  nickname: string
  profileImage?: string
  level: number
  experience: number
  wmtiType?: string
  role: string
}

export interface AuthTokenResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
  user: AuthUser
}

export const authService = {
  login: async (data: LoginRequest) => {
    const response = await apiClient.post<{ data: AuthTokenResponse }>('/auth/login', data)
    return response.data.data
  },

  register: async (data: RegisterRequest) => {
    const response = await apiClient.post<{ data: AuthTokenResponse }>('/auth/signup', data)
    return response.data.data
  },

  refreshToken: async (refreshToken: string) => {
    const response = await apiClient.post<{ data: AuthTokenResponse }>('/auth/refresh', {
      refreshToken,
    })
    return response.data.data
  },

  checkLoginIdAvailable: async (loginId: string) => {
    const response = await apiClient.get<{ data: { available: boolean } }>(`/auth/check-login-id?loginId=${encodeURIComponent(loginId)}`)
    return response.data.data.available
  },

  checkEmailAvailable: async (email: string) => {
    const response = await apiClient.get<{ data: { available: boolean } }>(`/auth/check-email?email=${encodeURIComponent(email)}`)
    return response.data.data.available
  },

  checkNicknameAvailable: async (nickname: string) => {
    const response = await apiClient.get<{ data: { available: boolean } }>(`/auth/check-nickname?nickname=${encodeURIComponent(nickname)}`)
    return response.data.data.available
  },
}
