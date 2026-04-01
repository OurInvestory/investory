import apiClient from './api'

export interface WmtiOption {
  id: number
  text: string
}

export interface WmtiQuestion {
  id: number
  question: string
  options: WmtiOption[]
}

export interface WmtiQuestionSet {
  questions: WmtiQuestion[]
  totalQuestions: number
}

export interface WmtiSubmitRequest {
  answers: { questionId: number; optionId: number }[]
}

export interface WmtiResultResponse {
  id: number
  resultType: string
  typeName: string
  stabilityScore: number
  growthScore: number
  riskScore: number
  incomeScore: number
  description: string
  recommendation: string
  traits: string[]
  createdAt: string
}

export const wmtiService = {
  getQuestions: async (): Promise<WmtiQuestion[]> => {
    const response = await apiClient.get<{ data: WmtiQuestionSet }>('/wmti/questions')
    return response.data.data.questions
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
