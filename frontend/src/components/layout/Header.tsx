import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Search, Bell, Menu } from 'lucide-react';
import { useAuthStore } from '@/stores/authStore';

export default function Header() {
  const { isAuthenticated } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  // 헤더를 숨겨야 하는 페이지들
  const hideHeaderPaths = ['/onboarding', '/login', '/register'];
  if (hideHeaderPaths.some(path => location.pathname.startsWith(path))) {
    return null;
  }

  return (
    <header className="bg-white dark:bg-dark-bg-primary border-b border-gray-100 dark:border-dark-border sticky top-0 z-50 safe-top">
      <div className="max-w-lg mx-auto px-4">
        <div className="flex justify-between items-center h-14">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">W</span>
            </div>
            <span className="text-xl font-bold text-primary-500">Investory</span>
          </Link>

          {/* Actions */}
          <div className="flex items-center gap-1">
            <button 
              onClick={() => navigate('/search')}
              className="p-2 text-gray-600 dark:text-dark-text-secondary hover:bg-gray-100 dark:hover:bg-dark-bg-secondary rounded-full transition-colors"
            >
              <Search className="w-5 h-5" />
            </button>
            
            {isAuthenticated && (
              <button 
                onClick={() => navigate('/notifications')}
                className="p-2 text-gray-600 dark:text-dark-text-secondary hover:bg-gray-100 dark:hover:bg-dark-bg-secondary rounded-full transition-colors relative"
              >
                <Bell className="w-5 h-5" />
                <span className="absolute top-1 right-1 w-2 h-2 bg-danger-500 rounded-full" />
              </button>
            )}
            
            <button 
              onClick={() => navigate('/menu')}
              className="p-2 text-gray-600 dark:text-dark-text-secondary hover:bg-gray-100 dark:hover:bg-dark-bg-secondary rounded-full transition-colors"
            >
              <Menu className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </header>
  );
}
