import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { Eye, EyeOff } from 'lucide-react'
import toast from 'react-hot-toast'
import { authService, LoginRequest } from '@/services/authService'
import { useAuthStore } from '@/stores/authStore'

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const { setAuth } = useAuthStore()

  const from = (location.state as { from?: { pathname: string } })?.from?.pathname || '/'

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequest>()

  const onSubmit = async (data: LoginRequest) => {
    setIsLoading(true)
    try {
      const response = await authService.login(data)
      setAuth(response.user, response.accessToken, response.refreshToken)
      toast.success('로그인되었습니다.')
      navigate(from, { replace: true })
    } catch (error) {
      toast.error('아이디 또는 비밀번호가 일치하지 않습니다.')
      console.error(error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleSocialLogin = (provider: 'google' | 'kakao') => {
    window.location.href = `/api/oauth2/authorization/${provider}`
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <Link to="/" className="text-3xl font-bold text-primary-600">
            Investory
          </Link>
          <h2 className="mt-6 text-2xl font-bold text-gray-900">로그인</h2>
          <p className="mt-2 text-gray-600">
            계정이 없으신가요?{' '}
            <Link to="/register" className="text-primary-600 hover:underline">
              회원가입
            </Link>
          </p>
        </div>

        <div className="card">
          <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
            <div>
              <label htmlFor="loginId" className="label">
                아이디
              </label>
              <input
                id="loginId"
                type="text"
                autoComplete="username"
                className={errors.loginId ? 'input-error' : 'input'}
                placeholder="아이디를 입력하세요"
                {...register('loginId', {
                  required: '아이디를 입력해주세요.',
                })}
              />
              {errors.loginId && (
                <p className="mt-1 text-sm text-danger-500">{errors.loginId.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="password" className="label">
                비밀번호
              </label>
              <div className="relative">
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  autoComplete="current-password"
                  className={errors.password ? 'input-error pr-10' : 'input pr-10'}
                  placeholder="비밀번호를 입력하세요"
                  {...register('password', {
                    required: '비밀번호를 입력해주세요.',
                  })}
                />
                <button
                  type="button"
                  className="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
              {errors.password && (
                <p className="mt-1 text-sm text-danger-500">{errors.password.message}</p>
              )}
            </div>

            <div className="flex items-center justify-between">
              <Link
                to="/find-id"
                className="text-sm text-gray-600 hover:text-primary-600"
              >
                아이디 찾기
              </Link>
              <Link
                to="/find-password"
                className="text-sm text-gray-600 hover:text-primary-600"
              >
                비밀번호 찾기
              </Link>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full btn-primary py-3"
            >
              {isLoading ? '로그인 중...' : '로그인'}
            </button>
          </form>

          <div className="mt-6">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300" />
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">또는</span>
              </div>
            </div>

            <div className="mt-6 space-y-3">
              <button
                type="button"
                onClick={() => handleSocialLogin('google')}
                className="w-full flex items-center justify-center px-4 py-3 border border-gray-300 rounded-lg shadow-sm bg-white hover:bg-gray-50 transition-colors"
              >
                <img
                  src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                  alt="Google"
                  className="w-5 h-5 mr-2"
                />
                <span className="text-gray-700 font-medium">Google로 계속하기</span>
              </button>

              <button
                type="button"
                onClick={() => handleSocialLogin('kakao')}
                className="w-full flex items-center justify-center px-4 py-3 rounded-lg shadow-sm bg-[#FEE500] hover:bg-[#FDD835] transition-colors"
              >
                <svg
                  className="w-5 h-5 mr-2"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                >
                  <path d="M12 3C6.477 3 2 6.463 2 10.689c0 2.691 1.697 5.051 4.225 6.428l-.894 3.323a.5.5 0 00.77.525l3.832-2.56a12.89 12.89 0 002.067.166c5.523 0 10-3.463 10-7.882C22 6.463 17.523 3 12 3z" />
                </svg>
                <span className="text-gray-900 font-medium">카카오로 계속하기</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
