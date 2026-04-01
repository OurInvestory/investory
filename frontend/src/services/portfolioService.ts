import apiClient from './api'

export interface HoldingResponse {
  id: number
  stockCode: string
  stockName: string
  market: string
  quantity: number
  averagePrice: number
  currentPrice: number
  totalValue: number
  totalInvestment: number
  profitLoss: number
  profitLossRate: number
  dailyProfitLoss: number
  weight: number
}

export interface PortfolioResponse {
  totalValue: number
  totalInvestment: number
  totalProfitLoss: number
  totalProfitLossRate: number
  dailyProfitLoss: number
  dailyProfitLossRate: number
  holdings: HoldingResponse[]
}

export const portfolioService = {
  get: async () => {
    const response = await apiClient.get<{ data: PortfolioResponse }>('/portfolio')
    return response.data.data
  },

  getByMarket: async (marketType: string) => {
    const response = await apiClient.get<{ data: PortfolioResponse }>(`/portfolio/${marketType}`)
    return response.data.data
  },
}
