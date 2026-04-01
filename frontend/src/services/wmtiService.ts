import apiClient from './api'

export interface WmtiQuestion {
  id: number
  question: string
  options: { label: string; value: number }[]
}

export interface WmtiSubmitRequest {
  answers: { questionId: number; value: number }[]
}

export interface WmtiResultResponse {
  id: number
  type: string
  typeName: string
  typeDescription: string
  scores: Record<string, number>
  createdAt: string
}

export const wmtiService = {
  getQuestions: async () => {
    const response = await apiClient.get<{ data: WmtiQuestion[] }>('/wmti/questions')
    return response.data.data
  },

  submit: async (data: WmtiSubmitRequest) => {
    const response = await apiClient.post<{ data: WmtiResultResponse }>('/wmti/submit', data)
    return response.data.data
  },

  getResult: async () => {
    const response = await apiClient.get<{ data: WmtiResultResponse }>('/wmti/result')
    return response.data.data
  },

  getResults: async () => {
    const response = await apiClient.get<{ data: WmtiResultResponse[] }>('/wmti/results')
    return response.data.data
  },
}
