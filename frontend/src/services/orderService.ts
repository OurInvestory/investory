import apiClient from './api'

export interface OrderRequest {
  stockCode: string
  side: 'BUY' | 'SELL'
  orderType: 'MARKET' | 'LIMIT'
  quantity: number
  price?: number
}

export interface OrderResponse {
  id: number
  stockCode: string
  stockName: string
  type: 'BUY' | 'SELL'
  orderType: 'MARKET' | 'LIMIT'
  status: 'PENDING' | 'PARTIALLY_FILLED' | 'FILLED' | 'CANCELLED' | 'REJECTED'
  quantity: number
  filledQuantity: number
  price: number
  filledPrice: number
  totalAmount: number
  createdAt: string
  updatedAt: string
}

export const orderService = {
  create: async (order: OrderRequest) => {
    const response = await apiClient.post<{ data: OrderResponse }>('/orders', order)
    return response.data.data
  },

  cancel: async (orderId: number) => {
    const response = await apiClient.post<{ data: OrderResponse }>(`/orders/${orderId}/cancel`)
    return response.data.data
  },

  getAll: async (page = 0, size = 20) => {
    const response = await apiClient.get(`/orders?page=${page}&size=${size}`)
    return response.data.data
  },

  getById: async (orderId: number) => {
    const response = await apiClient.get<{ data: OrderResponse }>(`/orders/${orderId}`)
    return response.data.data
  },

  getPending: async () => {
    const response = await apiClient.get<{ data: OrderResponse[] }>('/orders/pending')
    return response.data.data
  },
}
