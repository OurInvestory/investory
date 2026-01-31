import { useAuthStore } from '@/stores/authStore'

export default function MyPage() {
  const { user } = useAuthStore()

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">마이페이지</h1>

      <div className="card mb-6">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">프로필 정보</h2>
        <div className="space-y-3">
          <div className="flex justify-between py-2 border-b border-gray-100">
            <span className="text-gray-600">닉네임</span>
            <span className="font-medium">{user?.nickname}</span>
          </div>
          <div className="flex justify-between py-2 border-b border-gray-100">
            <span className="text-gray-600">이메일</span>
            <span className="font-medium">{user?.email}</span>
          </div>
          <div className="flex justify-between py-2 border-b border-gray-100">
            <span className="text-gray-600">아이디</span>
            <span className="font-medium">{user?.loginId}</span>
          </div>
          {user?.gender && (
            <div className="flex justify-between py-2 border-b border-gray-100">
              <span className="text-gray-600">성별</span>
              <span className="font-medium">
                {user.gender === 'M' ? '남성' : '여성'}
              </span>
            </div>
          )}
          {user?.birthYear && (
            <div className="flex justify-between py-2 border-b border-gray-100">
              <span className="text-gray-600">출생연도</span>
              <span className="font-medium">{user.birthYear}년</span>
            </div>
          )}
          {user?.wmtiType && (
            <div className="flex justify-between py-2">
              <span className="text-gray-600">투자 성향</span>
              <span className="badge-primary">{user.wmtiType}</span>
            </div>
          )}
        </div>
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="font-semibold text-gray-900 mb-2">관심 상품</h3>
          <p className="text-gray-500 text-sm">저장한 관심 상품이 없습니다.</p>
        </div>
        <div className="card">
          <h3 className="font-semibold text-gray-900 mb-2">최근 본 상품</h3>
          <p className="text-gray-500 text-sm">최근 본 상품이 없습니다.</p>
        </div>
      </div>
    </div>
  )
}
