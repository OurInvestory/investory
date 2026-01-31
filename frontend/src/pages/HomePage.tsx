import { Link } from 'react-router-dom'
import { ArrowRight, TrendingUp, Shield, Target, Users } from 'lucide-react'

export default function HomePage() {
  const features = [
    {
      icon: Target,
      title: '맞춤형 추천',
      description: 'WMTI 투자 성향 분석을 통해 나에게 딱 맞는 금융 상품을 추천받으세요.',
    },
    {
      icon: TrendingUp,
      title: '금융상품 비교',
      description: '예금, 적금, 펀드 등 다양한 금융 상품을 한눈에 비교하세요.',
    },
    {
      icon: Shield,
      title: '안전한 투자',
      description: '리스크 분석과 투자 가이드로 안전한 투자를 도와드립니다.',
    },
    {
      icon: Users,
      title: '커뮤니티',
      description: '다른 투자자들과 정보를 공유하고 함께 성장하세요.',
    },
  ]

  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="bg-gradient-to-br from-primary-600 to-primary-800 text-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6">
              나에게 맞는 금융 상품,
              <br />
              <span className="text-primary-200">Investory</span>에서 찾으세요
            </h1>
            <p className="text-xl text-primary-100 mb-8 max-w-2xl mx-auto">
              투자 성향 분석부터 맞춤형 상품 추천까지,
              <br />
              쉽고 빠르게 금융 상품을 비교하고 선택하세요.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link
                to="/wmti"
                className="inline-flex items-center justify-center px-8 py-3 text-lg font-medium bg-white text-primary-600 rounded-lg hover:bg-gray-100 transition-colors"
              >
                투자 성향 분석하기
                <ArrowRight className="ml-2" size={20} />
              </Link>
              <Link
                to="/products"
                className="inline-flex items-center justify-center px-8 py-3 text-lg font-medium border-2 border-white text-white rounded-lg hover:bg-white/10 transition-colors"
              >
                금융상품 둘러보기
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              왜 Investory인가요?
            </h2>
            <p className="text-lg text-gray-600 max-w-2xl mx-auto">
              금융 초보자도 쉽게 시작할 수 있는 맞춤형 금융 서비스
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {features.map((feature) => (
              <div
                key={feature.title}
                className="card hover:shadow-lg transition-shadow"
              >
                <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center mb-4">
                  <feature.icon className="text-primary-600" size={24} />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                  {feature.title}
                </h3>
                <p className="text-gray-600">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-white">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            지금 바로 시작하세요
          </h2>
          <p className="text-lg text-gray-600 mb-8">
            회원가입 후 투자 성향 분석을 완료하면
            <br />
            맞춤형 금융 상품 추천을 받을 수 있습니다.
          </p>
          <Link
            to="/register"
            className="btn-primary text-lg px-8 py-3"
          >
            무료로 시작하기
          </Link>
        </div>
      </section>
    </div>
  )
}
