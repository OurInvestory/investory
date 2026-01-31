import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { Eye, EyeOff, ChevronLeft } from 'lucide-react';
import toast from 'react-hot-toast';
import { authService, LoginRequest } from '@/services/authService';
import { useAuthStore } from '@/stores/authStore';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';

// 소셜 로그인 아이콘들
const NaverIcon = () => (
  <div className="w-11 h-11 bg-[#03C75A] rounded-full flex items-center justify-center">
    <span className="text-white font-bold text-lg">N</span>
  </div>
);

const KakaoIcon = () => (
  <div className="w-11 h-11 bg-[#FEE500] rounded-full flex items-center justify-center">
    <svg className="w-6 h-6" viewBox="0 0 24 24" fill="#3C1E1E">
      <path d="M12 3c5.8 0 10.5 3.66 10.5 8.18 0 4.52-4.7 8.18-10.5 8.18-.88 0-1.73-.09-2.55-.25L5.7 21.94c-.27.18-.64-.03-.58-.35l.88-3.63C3.6 16.33 1.5 13.44 1.5 11.18 1.5 6.66 6.2 3 12 3z"/>
    </svg>
  </div>
);

const GoogleIcon = () => (
  <div className="w-11 h-11 bg-white border border-gray-200 rounded-full flex items-center justify-center">
    <svg className="w-5 h-5" viewBox="0 0 24 24">
      <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
      <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
      <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
      <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
    </svg>
  </div>
);

const FacebookIcon = () => (
  <div className="w-11 h-11 bg-[#1877F2] rounded-full flex items-center justify-center">
    <svg className="w-6 h-6 text-white" fill="currentColor" viewBox="0 0 24 24">
      <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
    </svg>
  </div>
);

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { setAuth } = useAuthStore();

  const from = (location.state as { from?: { pathname: string } })?.from?.pathname || '/';

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequest>();

  const onSubmit = async (data: LoginRequest) => {
    setIsLoading(true);
    try {
      const response = await authService.login(data);
      setAuth(response.user, response.accessToken, response.refreshToken);
      toast.success('로그인되었습니다.');
      navigate(from, { replace: true });
    } catch {
      toast.error('아이디 또는 비밀번호가 일치하지 않습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSocialLogin = (provider: string) => {
    toast.success(`${provider} 로그인은 준비 중입니다.`);
  };

  return (
    <div className="min-h-screen bg-white dark:bg-dark-bg-primary flex flex-col">
      {/* Header */}
      <div className="flex items-center p-4">
        <button onClick={() => navigate(-1)} className="p-2 -ml-2">
          <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
        </button>
      </div>

      <div className="flex-1 px-6 py-4">
        {/* Logo */}
        <div className="flex flex-col items-center mb-8">
          <div className="w-16 h-16 bg-primary-100 rounded-2xl flex items-center justify-center mb-4">
            <span className="text-3xl">📊</span>
          </div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-dark-text-primary">로그인</h1>
        </div>

        {/* Form */}
        <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
          <Input
            label="아이디"
            type="text"
            placeholder="아이디를 입력하세요"
            error={errors.loginId?.message}
            {...register('loginId', {
              required: '아이디를 입력해주세요.',
            })}
          />

          <div className="relative">
            <Input
              label="비밀번호"
              type={showPassword ? 'text' : 'password'}
              placeholder="비밀번호를 입력하세요"
              error={errors.password?.message}
              rightIcon={
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              }
              {...register('password', {
                required: '비밀번호를 입력해주세요.',
              })}
            />
          </div>

          {/* 자동 로그인 & 아이디/비밀번호 찾기 */}
          <div className="flex items-center justify-between text-sm">
            <label className="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" className="w-4 h-4 rounded border-gray-300 text-primary-500 focus:ring-primary-500" />
              <span className="text-gray-600 dark:text-dark-text-secondary">자동 로그인</span>
            </label>
            <div className="flex items-center gap-2 text-gray-500 dark:text-dark-text-secondary">
              <Link to="/find-id" className="hover:text-gray-700 dark:hover:text-dark-text-primary">아이디 찾기</Link>
              <span>|</span>
              <Link to="/find-password" className="hover:text-gray-700 dark:hover:text-dark-text-primary">비밀번호 찾기</Link>
            </div>
          </div>

          {/* 로그인 버튼 */}
          <Button
            type="submit"
            variant="primary"
            fullWidth
            isLoading={isLoading}
            className="mt-6"
          >
            로그인
          </Button>
        </form>

        {/* 소셜 로그인 */}
        <div className="mt-8">
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-gray-200 dark:border-dark-border" />
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-4 bg-white dark:bg-dark-bg-primary text-gray-500 dark:text-dark-text-secondary">
                SNS 계정으로 로그인
              </span>
            </div>
          </div>

          <div className="flex justify-center gap-4 mt-6">
            <button onClick={() => handleSocialLogin('네이버')} className="hover:opacity-80 transition-opacity">
              <NaverIcon />
            </button>
            <button onClick={() => handleSocialLogin('카카오')} className="hover:opacity-80 transition-opacity">
              <KakaoIcon />
            </button>
            <button onClick={() => handleSocialLogin('구글')} className="hover:opacity-80 transition-opacity">
              <GoogleIcon />
            </button>
            <button onClick={() => handleSocialLogin('페이스북')} className="hover:opacity-80 transition-opacity">
              <FacebookIcon />
            </button>
          </div>
        </div>

        {/* 회원가입 */}
        <div className="mt-8 text-center">
          <p className="text-gray-600 dark:text-dark-text-secondary">
            아직 회원이 아니신가요?{' '}
            <Link to="/register" className="text-primary-500 font-medium hover:underline">
              회원가입
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
