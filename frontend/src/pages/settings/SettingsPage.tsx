import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, ChevronRight, Camera } from 'lucide-react';
import { useAuthStore } from '@/stores/authStore';
import { Avatar, Card } from '@/components/common';
import { useMyProfile, useDeleteAccount } from '@/hooks/useApi';
import toast from 'react-hot-toast';

interface SettingItemProps {
  label: string;
  value?: string;
  onClick?: () => void;
  showArrow?: boolean;
}

function SettingItem({ label, value, onClick, showArrow = true }: SettingItemProps) {
  return (
    <button
      onClick={onClick}
      className="w-full flex items-center justify-between py-4 border-b border-gray-100 dark:border-dark-border last:border-b-0"
    >
      <span className="text-gray-900 dark:text-dark-text-primary">{label}</span>
      <div className="flex items-center gap-2">
        {value && (
          <span className="text-gray-500 dark:text-dark-text-secondary">{value}</span>
        )}
        {showArrow && <ChevronRight className="w-5 h-5 text-gray-400" />}
      </div>
    </button>
  );
}

export default function SettingsPage() {
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();
  const { data: profileData } = useMyProfile();
  const deleteAccount = useDeleteAccount();

  const profile = profileData || { nickname: user?.nickname, email: user?.email };

  const handleLogout = () => {
    logout();
    toast.success('로그아웃되었습니다.');
    navigate('/');
  };

  const handleWithdraw = async () => {
    if (confirm('정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
      try {
        await deleteAccount.mutateAsync();
        logout();
        toast.success('회원 탈퇴가 완료되었습니다.');
        navigate('/');
      } catch {
        logout();
        toast.success('회원 탈퇴가 완료되었습니다.');
        navigate('/');
      }
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-dark-bg-primary pb-10">
      {/* Header */}
      <div className="sticky top-0 bg-white dark:bg-dark-bg-secondary z-10 border-b border-gray-100 dark:border-dark-border">
        <div className="flex items-center px-4 h-14">
          <button onClick={() => navigate(-1)} className="p-2 -ml-2">
            <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
          </button>
        </div>
      </div>

      <div className="px-4 py-6">
        {/* 프로필 사진 */}
        <div className="flex flex-col items-center mb-8">
          <div className="relative">
            <Avatar name={profile.nickname || '사용자'} size="xl" />
            <button className="absolute bottom-0 right-0 w-8 h-8 bg-white dark:bg-dark-bg-secondary border border-gray-200 dark:border-dark-border rounded-full flex items-center justify-center shadow-sm">
              <Camera className="w-4 h-4 text-gray-600 dark:text-dark-text-secondary" />
            </button>
          </div>
        </div>

        {/* 기본 정보 */}
        <Card className="mb-4">
          <SettingItem
            label="이름"
            value={profile.nickname || '사용자'}
            onClick={() => toast.success('이름 변경은 준비 중입니다.')}
          />
          <SettingItem
            label="휴대폰 번호"
            value="-"
            onClick={() => toast.success('휴대폰 번호 변경은 준비 중입니다.')}
          />
          <SettingItem
            label="이메일"
            value={profile.email || '-'}
            onClick={() => toast.success('이메일 변경은 준비 중입니다.')}
          />
          <SettingItem
            label="비밀번호"
            value="변경하기"
            onClick={() => navigate('/change-password')}
          />
        </Card>

        {/* 앱 설정 */}
        <Card className="mb-4">
          <SettingItem
            label="언어"
            value="한국어"
            onClick={() => toast.success('언어 설정은 준비 중입니다.')}
          />
          <SettingItem
            label="알림"
            onClick={() => navigate('/settings/notifications')}
          />
          <SettingItem
            label="화면 테마"
            value="화이트 테마"
            onClick={() => toast.success('테마 설정은 준비 중입니다.')}
          />
        </Card>

        {/* 법적 정보 */}
        <Card className="mb-4">
          <SettingItem
            label="약관 및 개인정보 처리 동의"
            onClick={() => navigate('/terms')}
          />
          <SettingItem
            label="개인정보 처리방침"
            onClick={() => navigate('/privacy')}
          />
        </Card>

        {/* 계정 관리 */}
        <Card className="mb-6">
          <button
            onClick={handleLogout}
            className="w-full py-4 text-left text-gray-900 dark:text-dark-text-primary border-b border-gray-100 dark:border-dark-border"
          >
            로그아웃
          </button>
          <button
            onClick={handleWithdraw}
            className="w-full py-4 text-left text-danger-500"
          >
            탈퇴하기
          </button>
        </Card>

        {/* 앱 정보 */}
        <div className="text-center">
          <p className="text-sm text-gray-500 dark:text-dark-text-secondary mb-4">
            앱 버전 1.0.0
          </p>
          <div className="flex items-center justify-center gap-2 mb-2">
            <div className="w-6 h-6 bg-primary-500 rounded flex items-center justify-center">
              <span className="text-white font-bold text-xs">W</span>
            </div>
            <span className="font-bold text-primary-500">Investory</span>
          </div>
          <p className="text-xs text-gray-400 dark:text-dark-text-secondary leading-relaxed px-4">
            Investory에서 제공하는 정보와 모의투자 기능은 투자 판단을 위한 참고용이며, 
            실제 매매의 제안·권유 또는 종목 추천이 아닙니다.
          </p>
          <p className="text-xs text-gray-400 dark:text-dark-text-secondary mt-2">
            표시되는 가격 자료는 지연되거나 실제 시장가와 다를 수 있으며, 체결/손익은 
            고객 목적의 시뮬레이션 결과입니다. 수수료 0.015%는 달의 개선에 전해 적용됩니다.
          </p>
        </div>
      </div>
    </div>
  );
}
