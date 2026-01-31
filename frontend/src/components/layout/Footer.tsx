import { Link } from 'react-router-dom'

export default function Footer() {
  const currentYear = new Date().getFullYear()

  return (
    <footer className="bg-gray-900 text-gray-300">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand */}
          <div className="col-span-1 md:col-span-2">
            <h3 className="text-2xl font-bold text-white mb-4">Investory</h3>
            <p className="text-gray-400 mb-4 max-w-md">
              개인 맞춤형 금융 상품 추천 플랫폼입니다.
              투자 성향에 맞는 최적의 금융 상품을 찾아보세요.
            </p>
          </div>

          {/* Links */}
          <div>
            <h4 className="text-white font-semibold mb-4">서비스</h4>
            <ul className="space-y-2">
              <li>
                <Link to="/products" className="hover:text-white transition-colors">
                  금융상품
                </Link>
              </li>
              <li>
                <Link to="/wmti" className="hover:text-white transition-colors">
                  투자성향분석
                </Link>
              </li>
              <li>
                <Link to="/community" className="hover:text-white transition-colors">
                  커뮤니티
                </Link>
              </li>
            </ul>
          </div>

          {/* Support */}
          <div>
            <h4 className="text-white font-semibold mb-4">고객지원</h4>
            <ul className="space-y-2">
              <li>
                <a href="#" className="hover:text-white transition-colors">
                  이용약관
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-white transition-colors">
                  개인정보처리방침
                </a>
              </li>
              <li>
                <a
                  href="https://forms.google.com"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="hover:text-white transition-colors"
                >
                  피드백 보내기
                </a>
              </li>
            </ul>
          </div>
        </div>

        <div className="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400 text-sm">
          <p>&copy; {currentYear} Investory. All rights reserved.</p>
        </div>
      </div>
    </footer>
  )
}
