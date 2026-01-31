import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Suspense, lazy } from 'react'
import Layout from '@/components/layout/Layout'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import ProtectedRoute from '@/components/auth/ProtectedRoute'

// Lazy loaded pages
const HomePage = lazy(() => import('@/pages/HomePage'))
const LoginPage = lazy(() => import('@/pages/auth/LoginPage'))
const RegisterPage = lazy(() => import('@/pages/auth/RegisterPage'))
const WMTIPage = lazy(() => import('@/pages/wmti/WMTIPage'))
const ProductsPage = lazy(() => import('@/pages/products/ProductsPage'))
const ProductDetailPage = lazy(() => import('@/pages/products/ProductDetailPage'))
const MyPage = lazy(() => import('@/pages/mypage/MyPage'))
const NotFoundPage = lazy(() => import('@/pages/NotFoundPage'))

function App() {
  return (
    <BrowserRouter>
      <Suspense fallback={<LoadingSpinner fullScreen />}>
        <Routes>
          {/* Public routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Routes with Layout */}
          <Route element={<Layout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/products" element={<ProductsPage />} />
            <Route path="/products/:id" element={<ProductDetailPage />} />
            <Route path="/wmti" element={<WMTIPage />} />

            {/* Protected routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="/mypage" element={<MyPage />} />
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
