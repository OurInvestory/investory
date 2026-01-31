import { NavLink, useLocation } from 'react-router-dom';
import { Home, TrendingUp, Heart, Compass, MessageCircle, User } from 'lucide-react';
import { cn } from '@/utils';
import { useAuthStore } from '@/stores/authStore';

const navItems = [
  { path: '/', icon: Home, label: '증권홈' },
  { path: '/watchlist', icon: Heart, label: '관심' },
  { path: '/discover', icon: Compass, label: '발견' },
  { path: '/community', icon: MessageCircle, label: '소통' },
  { path: '/mypage', icon: User, label: '마이' },
];

export default function BottomNavigation() {
  const location = useLocation();
  const { isAuthenticated } = useAuthStore();

  // 네비게이션을 숨겨야 하는 페이지들
  const hideNavPaths = ['/onboarding', '/login', '/register', '/wmti', '/trade', '/stock/'];
  if (hideNavPaths.some(path => location.pathname.startsWith(path))) {
    return null;
  }

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white dark:bg-dark-bg-primary border-t border-gray-100 dark:border-dark-border z-50 safe-bottom">
      <div className="max-w-lg mx-auto">
        <div className="flex items-center justify-around h-16">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path || 
              (item.path !== '/' && location.pathname.startsWith(item.path));

            // 로그인 필요한 페이지 체크
            const requiresAuth = ['/mypage', '/watchlist'].includes(item.path);
            const to = requiresAuth && !isAuthenticated ? '/login' : item.path;

            return (
              <NavLink
                key={item.path}
                to={to}
                className={cn(
                  'flex flex-col items-center justify-center flex-1 py-2 transition-colors',
                  isActive
                    ? 'text-primary-500'
                    : 'text-gray-400 hover:text-gray-600 dark:hover:text-dark-text-secondary'
                )}
              >
                <Icon className="w-6 h-6 mb-1" strokeWidth={isActive ? 2.5 : 2} />
                <span className="text-xs font-medium">{item.label}</span>
              </NavLink>
            );
          })}
        </div>
      </div>
    </nav>
  );
}
