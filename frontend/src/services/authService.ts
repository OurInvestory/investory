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
  gender?: string
  birthYear?: number
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  user: {
    id: number
    loginId: string
    email: string
    nickname: string
    gender?: string
    birthYear?: number
    wmtiType?: string
  }
}

export const authService = {
  login: async (data: LoginRequest) => {
    const response = await apiClient.post<{ data: AuthResponse }>('/auth/sessions', data)
    return response.data.data
  },

  register: async (data: RegisterRequest) => {
    const response = await apiClient.post('/auth/registrations', data)
    return response.data
  },

  logout: async () => {
    const response = await apiClient.delete('/auth/sessions/current')
    return response.data
  },

  refreshToken: async (refreshToken: string) => {
    const response = await apiClient.post<{ data: AuthResponse }>('/auth/tokens/refresh', {
      refreshToken,
    })
    return response.data.data
  },

  checkAvailability: async (loginId?: string, email?: string) => {
    const params = new URLSearchParams()
    if (loginId) params.append('loginId', loginId)
    if (email) params.append('email', email)
    const response = await apiClient.get(`/auth/registrations/availability?${params}`)
    return response.data.data
  },

  requestEmailVerification: async (email: string) => {
    const response = await apiClient.post('/auth/email-verifications', { email })
    return response.data
  },

  verifyEmail: async (verificationId: string, code: string) => {
    const response = await apiClient.put(`/auth/email-verifications/${verificationId}`, {
      code,
    })
    return response.data
  },

  findId: async (name: string, email: string) => {
    const response = await apiClient.post('/auth/find-id', { name, email })
    return response.data
  },

  findPassword: async (loginId: string, email: string) => {
    const response = await apiClient.post('/auth/find-password', { loginId, email })
    return response.data
  },

  resetPassword: async (token: string, newPassword: string) => {
    const response = await apiClient.post('/auth/reset-password', { token, newPassword })
    return response.data
  },
}
