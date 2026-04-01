import apiClient from './api'

export interface StockSummary {
  id: number
  code: string
  name: string
  market: string
  currentPrice: number
  changeRate: number
  changeAmount: number
  volume: number
  logoUrl?: string
}

export interface StockDetail extends StockSummary {
  englishName?: string
  sector?: string
  previousClose: number
  high52Week: number
  low52Week: number
  marketCap: number
}

export interface OrderbookEntry {
  price: number
  quantity: number
  count: number
}

export interface Orderbook {
  stockCode: string
  asks: OrderbookEntry[]
  bids: OrderbookEntry[]
}

export interface WatchlistItem {
  id: number
  groupName: string
  stock: StockSummary
  sortOrder: number
}

export const stockService = {
  getAll: async (market?: string) => {
    const params = market ? `?market=${market}` : ''
    const response = await apiClient.get<{ data: StockSummary[] }>(`/stocks${params}`)
    return response.data.data
  },

  getDetail: async (code: string) => {
    const response = await apiClient.get<{ data: StockDetail }>(`/stocks/${code}`)
    return response.data.data
  },

  search: async (keyword: string, page = 0, size = 20) => {
    const response = await apiClient.get(`/stocks/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`)
    return response.data.data
  },

  getTop: async (type = 'volume', limit = 10) => {
    const response = await apiClient.get<{ data: StockSummary[] }>(`/stocks/top?type=${type}&limit=${limit}`)
    return response.data.data
  },

  getSectors: async () => {
    const response = await apiClient.get<{ data: string[] }>('/stocks/sectors')
    return response.data.data
  },

  getBySecter: async (sector: string) => {
    const response = await apiClient.get<{ data: StockSummary[] }>(`/stocks/sectors/${encodeURIComponent(sector)}`)
    return response.data.data
  },

  getOrderbook: async (code: string) => {
    const response = await apiClient.get<{ data: Orderbook }>(`/stocks/${code}/orderbook`)
    return response.data.data
  },

  // Watchlist
  getWatchlist: async (group?: string) => {
    const params = group ? `?group=${encodeURIComponent(group)}` : ''
    const response = await apiClient.get<{ data: WatchlistItem[] }>(`/stocks/watchlist${params}`)
    return response.data.data
  },

  getWatchlistGroups: async () => {
    const response = await apiClient.get<{ data: string[] }>('/stocks/watchlist/groups')
    return response.data.data
  },

  addToWatchlist: async (stockCode: string, groupName?: string) => {
    const response = await apiClient.post<{ data: WatchlistItem }>('/stocks/watchlist', {
      stockCode,
      groupName,
    })
    return response.data.data
  },

  removeFromWatchlist: async (code: string) => {
    await apiClient.delete(`/stocks/watchlist/${code}`)
  },

  isInWatchlist: async (code: string) => {
    const response = await apiClient.get<{ data: { inWatchlist: boolean } }>(`/stocks/watchlist/check/${code}`)
    return response.data.data.inWatchlist
  },
}
