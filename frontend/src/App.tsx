import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Suspense, lazy } from 'react'
import Layout from '@/components/layout/Layout'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import ProtectedRoute from '@/components/auth/ProtectedRoute'

// Lazy loaded pages
const HomePage = lazy(() => import('@/pages/HomePage'))
const LoginPage = lazy(() => import('@/pages/auth/LoginPage'))
const RegisterPage = lazy(() => import('@/pages/auth/RegisterPage'))
const OnboardingPage = lazy(() => import('@/pages/onboarding/OnboardingPage'))
const WMTIPage = lazy(() => import('@/pages/wmti/WMTIPage'))
const StockDetailPage = lazy(() => import('@/pages/stock/StockDetailPage'))
const SearchPage = lazy(() => import('@/pages/search/SearchPage'))
const TradePage = lazy(() => import('@/pages/trade/TradePage'))
const PortfolioPage = lazy(() => import('@/pages/portfolio/PortfolioPage'))
const MyPage = lazy(() => import('@/pages/mypage/MyPage'))
const SettingsPage = lazy(() => import('@/pages/settings/SettingsPage'))
const RewardsPage = lazy(() => import('@/pages/rewards/RewardsPage'))
const NotFoundPage = lazy(() => import('@/pages/NotFoundPage'))

function App() {
  return (
    <BrowserRouter>
      <Suspense fallback={<LoadingSpinner fullScreen />}>
        <Routes>
          {/* Public routes */}
          <Route path="/onboarding" element={<OnboardingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Routes with Layout */}
          <Route element={<Layout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/search" element={<SearchPage />} />
            <Route path="/stock/:code" element={<StockDetailPage />} />
            <Route path="/trade/:code" element={<TradePage />} />
            <Route path="/wmti" element={<WMTIPage />} />

            {/* Protected routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="/portfolio" element={<PortfolioPage />} />
              <Route path="/mypage" element={<MyPage />} />
              <Route path="/settings" element={<SettingsPage />} />
              <Route path="/rewards" element={<RewardsPage />} />
            </Route>
          </Route>

          {/* 404 */}
          <Route path="/404" element={<NotFoundPage />} />
          <Route path="*" element={<Navigate to="/404" replace />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  )
}

export default App
