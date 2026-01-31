import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { Eye, EyeOff, Check, X } from 'lucide-react'
import toast from 'react-hot-toast'
import { authService, RegisterRequest } from '@/services/authService'

interface RegisterFormData extends RegisterRequest {
  passwordConfirm: string
}

export default function RegisterPage() {
  const [showPassword, setShowPassword] = useState(false)
  const [showPasswordConfirm, setShowPasswordConfirm] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [isCheckingId, setIsCheckingId] = useState(false)
  const [isCheckingEmail, setIsCheckingEmail] = useState(false)
  const [idAvailable, setIdAvailable] = useState<boolean | null>(null)
  const [emailAvailable, setEmailAvailable] = useState<boolean | null>(null)
  const navigate = useNavigate()

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<RegisterFormData>()

  const password = watch('password')
  const loginId = watch('loginId')
  const email = watch('email')

  const checkIdAvailability = async () => {
    if (!loginId || loginId.length < 4) {
      toast.error('아이디는 4자 이상이어야 합니다.')
      return
    }
    setIsCheckingId(true)
    try {
      const result = await authService.checkAvailability(loginId)
      setIdAvailable(result.loginIdAvailable)
      if (result.loginIdAvailable) {
        toast.success('사용 가능한 아이디입니다.')
      } else {
        toast.error('이미 사용 중인 아이디입니다.')
      }
    } catch {
      toast.error('중복 확인에 실패했습니다.')
    } finally {
      setIsCheckingId(false)
    }
  }

  const checkEmailAvailability = async () => {
    if (!email) {
      toast.error('이메일을 입력해주세요.')
      return
    }
    setIsCheckingEmail(true)
    try {
      const result = await authService.checkAvailability(undefined, email)
      setEmailAvailable(result.emailAvailable)
      if (result.emailAvailable) {
        toast.success('사용 가능한 이메일입니다.')
      } else {
        toast.error('이미 사용 중인 이메일입니다.')
      }
    } catch {
      toast.error('중복 확인에 실패했습니다.')
    } finally {
      setIsCheckingEmail(false)
    }
  }

  const onSubmit = async (data: RegisterFormData) => {
    if (idAvailable !== true) {
      toast.error('아이디 중복 확인을 해주세요.')
      return
    }
    if (emailAvailable !== true) {
      toast.error('이메일 중복 확인을 해주세요.')
      return
    }

    setIsLoading(true)
    try {
      const { passwordConfirm: _, ...registerData } = data
      await authService.register(registerData)
      toast.success('회원가입이 완료되었습니다.')
      navigate('/login')
    } catch (error) {
      toast.error('회원가입에 실패했습니다.')
      console.error(error)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <Link to="/" className="text-3xl font-bold text-primary-600">
            Investory
          </Link>
          <h2 className="mt-6 text-2xl font-bold text-gray-900">회원가입</h2>
          <p className="mt-2 text-gray-600">
            이미 계정이 있으신가요?{' '}
            <Link to="/login" className="text-primary-600 hover:underline">
              로그인
            </Link>
          </p>
        </div>

        <div className="card">
          <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>
            {/* 아이디 */}
            <div>
              <label htmlFor="loginId" className="label">
                아이디 <span className="text-danger-500">*</span>
              </label>
              <div className="flex gap-2">
                <div className="flex-1 relative">
                  <input
                    id="loginId"
                    type="text"
                    className={errors.loginId ? 'input-error' : 'input'}
                    placeholder="4~20자 영문, 숫자"
                    {...register('loginId', {
                      required: '아이디를 입력해주세요.',
                      minLength: { value: 4, message: '4자 이상 입력해주세요.' },
                      maxLength: { value: 20, message: '20자 이하로 입력해주세요.' },
                      pattern: {
                        value: /^[a-zA-Z0-9]+$/,
                        message: '영문과 숫자만 사용 가능합니다.',
                      },
                      onChange: () => setIdAvailable(null),
                    })}
                  />
                  {idAvailable !== null && (
                    <span className="absolute right-3 top-1/2 -translate-y-1/2">
                      {idAvailable ? (
                        <Check className="text-success-500" size={20} />
                      ) : (
                        <X className="text-danger-500" size={20} />
                      )}
                    </span>
                  )}
                </div>
                <button
                  type="button"
                  onClick={checkIdAvailability}
                  disabled={isCheckingId}
                  className="btn-outline whitespace-nowrap"
                >
                  {isCheckingId ? '확인 중...' : '중복확인'}
                </button>
              </div>
              {errors.loginId && (
                <p className="mt-1 text-sm text-danger-500">{errors.loginId.message}</p>
              )}
            </div>

            {/* 이메일 */}
            <div>
              <label htmlFor="email" className="label">
                이메일 <span className="text-danger-500">*</span>
              </label>
              <div className="flex gap-2">
                <div className="flex-1 relative">
                  <input
                    id="email"
                    type="email"
                    className={errors.email ? 'input-error' : 'input'}
                    placeholder="example@email.com"
                    {...register('email', {
                      required: '이메일을 입력해주세요.',
                      pattern: {
                        value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                        message: '올바른 이메일 형식이 아닙니다.',
                      },
                      onChange: () => setEmailAvailable(null),
                    })}
                  />
                  {emailAvailable !== null && (
                    <span className="absolute right-3 top-1/2 -translate-y-1/2">
                      {emailAvailable ? (
                        <Check className="text-success-500" size={20} />
                      ) : (
                        <X className="text-danger-500" size={20} />
                      )}
                    </span>
                  )}
                </div>
                <button
                  type="button"
                  onClick={checkEmailAvailability}
                  disabled={isCheckingEmail}
                  className="btn-outline whitespace-nowrap"
                >
                  {isCheckingEmail ? '확인 중...' : '중복확인'}
                </button>
              </div>
              {errors.email && (
                <p className="mt-1 text-sm text-danger-500">{errors.email.message}</p>
              )}
            </div>

            {/* 닉네임 */}
            <div>
              <label htmlFor="nickname" className="label">
                닉네임 <span className="text-danger-500">*</span>
              </label>
              <input
                id="nickname"
                type="text"
                className={errors.nickname ? 'input-error' : 'input'}
                placeholder="2~10자"
                {...register('nickname', {
                  required: '닉네임을 입력해주세요.',
                  minLength: { value: 2, message: '2자 이상 입력해주세요.' },
                  maxLength: { value: 10, message: '10자 이하로 입력해주세요.' },
                })}
              />
              {errors.nickname && (
                <p className="mt-1 text-sm text-danger-500">{errors.nickname.message}</p>
              )}
            </div>

            {/* 비밀번호 */}
            <div>
              <label htmlFor="password" className="label">
                비밀번호 <span className="text-danger-500">*</span>
              </label>
              <div className="relative">
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  className={errors.password ? 'input-error pr-10' : 'input pr-10'}
                  placeholder="8자 이상, 영문/숫자/특수문자 포함"
                  {...register('password', {
                    required: '비밀번호를 입력해주세요.',
                    minLength: { value: 8, message: '8자 이상 입력해주세요.' },
                    pattern: {
                      value: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]+$/,
                      message: '영문, 숫자, 특수문자를 포함해주세요.',
                    },
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

            {/* 비밀번호 확인 */}
            <div>
              <label htmlFor="passwordConfirm" className="label">
                비밀번호 확인 <span className="text-danger-500">*</span>
              </label>
              <div className="relative">
                <input
                  id="passwordConfirm"
                  type={showPasswordConfirm ? 'text' : 'password'}
                  className={errors.passwordConfirm ? 'input-error pr-10' : 'input pr-10'}
                  placeholder="비밀번호를 다시 입력하세요"
                  {...register('passwordConfirm', {
                    required: '비밀번호 확인을 입력해주세요.',
                    validate: (value) =>
                      value === password || '비밀번호가 일치하지 않습니다.',
                  })}
                />
                <button
                  type="button"
                  className="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500"
                  onClick={() => setShowPasswordConfirm(!showPasswordConfirm)}
                >
                  {showPasswordConfirm ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
              {errors.passwordConfirm && (
                <p className="mt-1 text-sm text-danger-500">
                  {errors.passwordConfirm.message}
                </p>
              )}
            </div>

            {/* 성별 */}
            <div>
              <label className="label">성별</label>
              <div className="flex gap-4">
                <label className="flex items-center">
                  <input
                    type="radio"
                    value="M"
                    className="mr-2"
                    {...register('gender')}
                  />
                  남성
                </label>
                <label className="flex items-center">
                  <input
                    type="radio"
                    value="F"
                    className="mr-2"
                    {...register('gender')}
                  />
                  여성
                </label>
              </div>
            </div>

            {/* 출생연도 */}
            <div>
              <label htmlFor="birthYear" className="label">
                출생연도
              </label>
              <select
                id="birthYear"
                className="input"
                {...register('birthYear', { valueAsNumber: true })}
              >
                <option value="">선택하세요</option>
                {Array.from({ length: 80 }, (_, i) => new Date().getFullYear() - i).map(
                  (year) => (
                    <option key={year} value={year}>
                      {year}년
                    </option>
                  )
                )}
              </select>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full btn-primary py-3"
            >
              {isLoading ? '가입 중...' : '회원가입'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}
