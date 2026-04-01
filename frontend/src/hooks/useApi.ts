import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { stockService } from '../services/stockService'
import { orderService, type OrderRequest } from '../services/orderService'
import { portfolioService } from '../services/portfolioService'
import { wmtiService, type WmtiSubmitRequest } from '../services/wmtiService'
import { rewardService } from '../services/rewardService'
import { userService, type UpdateProfileRequest, type ChangePasswordRequest } from '../services/userService'

// ========== Stock Hooks ==========

export const useStocks = (market?: string) =>
  useQuery({
    queryKey: ['stocks', market],
    queryFn: () => stockService.getAll(market),
  })

export const useStockDetail = (code: string) =>
  useQuery({
    queryKey: ['stock', code],
    queryFn: () => stockService.getDetail(code),
    enabled: !!code,
  })

export const useStockSearch = (keyword: string) =>
  useQuery({
    queryKey: ['stocks', 'search', keyword],
    queryFn: () => stockService.search(keyword),
    enabled: keyword.length >= 1,
  })

export const useTopStocks = (type = 'volume', limit = 10) =>
  useQuery({
    queryKey: ['stocks', 'top', type, limit],
    queryFn: () => stockService.getTop(type, limit),
  })

export const useOrderbook = (code: string) =>
  useQuery({
    queryKey: ['orderbook', code],
    queryFn: () => stockService.getOrderbook(code),
    enabled: !!code,
    refetchInterval: 5000,
  })

export const useWatchlist = (group?: string) =>
  useQuery({
    queryKey: ['watchlist', group],
    queryFn: () => stockService.getWatchlist(group),
  })

export const useIsInWatchlist = (code: string) =>
  useQuery({
    queryKey: ['watchlist', 'check', code],
    queryFn: () => stockService.isInWatchlist(code),
    enabled: !!code,
  })

export const useAddToWatchlist = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ stockCode, groupName }: { stockCode: string; groupName?: string }) =>
      stockService.addToWatchlist(stockCode, groupName),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['watchlist'] })
    },
  })
}

export const useRemoveFromWatchlist = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (code: string) => stockService.removeFromWatchlist(code),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['watchlist'] })
    },
  })
}

// ========== Order Hooks ==========

export const useCreateOrder = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (order: OrderRequest) => orderService.create(order),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['portfolio'] })
    },
  })
}

export const useCancelOrder = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (orderId: number) => orderService.cancel(orderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
    },
  })
}

export const useOrders = (page = 0, size = 20) =>
  useQuery({
    queryKey: ['orders', page, size],
    queryFn: () => orderService.getAll(page, size),
  })

export const usePendingOrders = () =>
  useQuery({
    queryKey: ['orders', 'pending'],
    queryFn: () => orderService.getPending(),
  })

// ========== Portfolio Hooks ==========

export const usePortfolio = () =>
  useQuery({
    queryKey: ['portfolio'],
    queryFn: () => portfolioService.get(),
  })

export const usePortfolioByMarket = (marketType: string) =>
  useQuery({
    queryKey: ['portfolio', marketType],
    queryFn: () => portfolioService.getByMarket(marketType),
    enabled: !!marketType,
  })

// ========== WMTI Hooks ==========

export const useWmtiQuestions = () =>
  useQuery({
    queryKey: ['wmti', 'questions'],
    queryFn: () => wmtiService.getQuestions(),
  })

export const useWmtiResult = () =>
  useQuery({
    queryKey: ['wmti', 'result'],
    queryFn: () => wmtiService.getResult(),
    retry: false,
  })

export const useWmtiResults = () =>
  useQuery({
    queryKey: ['wmti', 'results'],
    queryFn: () => wmtiService.getResults(),
  })

export const useSubmitWmti = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: WmtiSubmitRequest) => wmtiService.submit(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['wmti'] })
    },
  })
}

// ========== Reward Hooks ==========

export const useLevelInfo = () =>
  useQuery({
    queryKey: ['rewards', 'level'],
    queryFn: () => rewardService.getLevel(),
  })

export const useAchievements = () =>
  useQuery({
    queryKey: ['rewards', 'achievements'],
    queryFn: () => rewardService.getAchievements(),
  })

// ========== User Hooks ==========

export const useMyProfile = () =>
  useQuery({
    queryKey: ['user', 'me'],
    queryFn: () => userService.getMe(),
  })

export const useUpdateProfile = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: UpdateProfileRequest) => userService.updateMe(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['user', 'me'] })
    },
  })
}

export const useChangePassword = () =>
  useMutation({
    mutationFn: (data: ChangePasswordRequest) => userService.changePassword(data),
  })

export const useDeleteAccount = () =>
  useMutation({
    mutationFn: () => userService.deleteAccount(),
  })
