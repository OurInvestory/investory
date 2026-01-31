import { Outlet, useLocation } from 'react-router-dom';
import Header from './Header';
import BottomNavigation from './BottomNavigation';

export default function Layout() {
  const location = useLocation();
  
  // 전체 화면을 사용하는 페이지들 (헤더/푸터 없음)
  const fullScreenPaths = ['/onboarding', '/login', '/register'];
  const isFullScreen = fullScreenPaths.some(path => location.pathname.startsWith(path));

  if (isFullScreen) {
    return <Outlet />;
  }

  return (
    <div className="min-h-screen flex flex-col bg-light-bg-primary dark:bg-dark-bg-primary">
      <Header />
      <main className="flex-1 pb-20 max-w-lg mx-auto w-full">
        <Outlet />
      </main>
      <BottomNavigation />
    </div>
  );
}
