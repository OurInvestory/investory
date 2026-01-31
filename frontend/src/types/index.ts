// User types
export interface User {
  id: number;
  email: string;
  name: string;
  nickname: string;
  phone?: string;
  profileImage?: string;
  level: number;
  experience: number;
  wmtiType?: string;
  createdAt: string;
}

// Stock types
export interface Stock {
  id: number;
  code: string;
  name: string;
  market: 'KOSPI' | 'KOSDAQ' | 'NASDAQ' | 'NYSE';
  currentPrice: number;
  previousClose: number;
  changePrice: number;
  changeRate: number;
  volume: number;
  marketCap?: number;
  high52Week?: number;
  low52Week?: number;
}

export interface StockDetail extends Stock {
  description?: string;
  sector?: string;
  per?: number;
  pbr?: number;
  eps?: number;
  dividendYield?: number;
  chartData?: ChartData[];
}

export interface ChartData {
  date: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}

// Portfolio types
export interface Portfolio {
  totalValue: number;
  totalInvested: number;
  totalReturn: number;
  totalReturnRate: number;
  dailyReturn: number;
  dailyReturnRate: number;
  holdings: Holding[];
}

export interface Holding {
  id: number;
  stock: Stock;
  quantity: number;
  averagePrice: number;
  totalValue: number;
  totalReturn: number;
  returnRate: number;
}

// Order types
export type OrderType = 'BUY' | 'SELL';
export type OrderPriceType = 'MARKET' | 'LIMIT';
export type OrderStatus = 'PENDING' | 'COMPLETED' | 'CANCELLED';

export interface Order {
  id: number;
  stock: Stock;
  type: OrderType;
  priceType: OrderPriceType;
  quantity: number;
  price: number;
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
  executedAt?: string;
}

// Transaction types
export interface Transaction {
  id: number;
  stock: Stock;
  type: OrderType;
  quantity: number;
  price: number;
  totalAmount: number;
  createdAt: string;
}

// Community types
export interface Post {
  id: number;
  author: User;
  stockCode?: string;
  stockName?: string;
  content: string;
  likeCount: number;
  commentCount: number;
  isLiked: boolean;
  createdAt: string;
}

export interface Comment {
  id: number;
  author: User;
  content: string;
  likeCount: number;
  isLiked: boolean;
  createdAt: string;
}

// WMTI types
export interface WMTIQuestion {
  id: number;
  question: string;
  options: WMTIOption[];
}

export interface WMTIOption {
  id: number;
  text: string;
  description?: string;
  score: number;
}

export interface WMTIResult {
  type: string;
  title: string;
  description: string;
  traits: string[];
  recommendations: string[];
  compatibility: string[];
}

// Achievement types
export interface Achievement {
  id: number;
  name: string;
  description: string;
  icon: string;
  isUnlocked: boolean;
  unlockedAt?: string;
  progress?: number;
  maxProgress?: number;
}

// Level types
export interface LevelInfo {
  level: number;
  currentExp: number;
  requiredExp: number;
  title: string;
}

// Watchlist types
export interface WatchlistGroup {
  id: number;
  name: string;
  stocks: Stock[];
}

// Market Index types
export interface MarketIndex {
  name: string;
  value: number;
  change: number;
  changeRate: number;
}

// Orderbook types
export interface OrderbookEntry {
  price: number;
  quantity: number;
  type: 'ask' | 'bid';
}

export interface Orderbook {
  asks: OrderbookEntry[];
  bids: OrderbookEntry[];
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}
