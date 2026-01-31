import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { TrendingUp, BarChart3, Users, ChevronRight } from 'lucide-react';
import Button from '@/components/common/Button';

const slides = [
  {
    icon: TrendingUp,
    title: '투자의 시작',
    subtitle: '나만의 포트폴리오를 만들어보세요',
    description: 'Investory와 함께 스마트한 투자 사례를 시작하세요. 초보자도 쉽게 할 수 있는 투자투자가 만들어집니다.',
    color: 'bg-primary-100',
    iconColor: 'text-primary-500',
  },
  {
    icon: BarChart3,
    title: '실시간 정보',
    subtitle: '정보 하나에서 시장 흐름 파악',
    description: '실시간 주가 정보와 시장 분석을 통해 최신 AI 투자를 습득할 수 있고 심층을 나만 수 있습니다.',
    color: 'bg-blue-100',
    iconColor: 'text-blue-500',
  },
  {
    icon: Users,
    title: '커뮤니티',
    subtitle: '투자자들과 경험 공유하세요',
    description: '다른 투자자들과 손쉽게를 공유하고 심층적인 정보와 이야기들을 공유할 수 있습니다.',
    color: 'bg-green-100',
    iconColor: 'text-green-500',
  },
];

export default function OnboardingPage() {
  const [currentSlide, setCurrentSlide] = useState(0);
  const navigate = useNavigate();

  const handleNext = () => {
    if (currentSlide < slides.length - 1) {
      setCurrentSlide(currentSlide + 1);
    } else {
      navigate('/login');
    }
  };

  const handleSkip = () => {
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-white dark:bg-dark-bg-primary flex flex-col">
      {/* Header */}
      <div className="flex justify-between items-center p-4">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-sm">W</span>
          </div>
          <span className="text-lg font-bold text-primary-500">Investory</span>
        </div>
        <button
          onClick={handleSkip}
          className="text-gray-500 dark:text-dark-text-secondary text-sm"
        >
          건너뛰기
        </button>
      </div>

      {/* Slides */}
      <div className="flex-1 flex flex-col items-center justify-center px-8">
        <AnimatePresence mode="wait">
          <motion.div
            key={currentSlide}
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            transition={{ duration: 0.3 }}
            className="text-center max-w-sm"
          >
            {/* Icon */}
            <div className={`w-24 h-24 ${slides[currentSlide].color} rounded-full flex items-center justify-center mx-auto mb-8`}>
              {(() => {
                const Icon = slides[currentSlide].icon;
                return <Icon className={`w-12 h-12 ${slides[currentSlide].iconColor}`} />;
              })()}
            </div>

            {/* Title */}
            <h1 className="text-2xl font-bold text-gray-900 dark:text-dark-text-primary mb-2">
              {slides[currentSlide].title}
            </h1>
            <p className="text-lg font-medium text-primary-500 mb-4">
              {slides[currentSlide].subtitle}
            </p>
            <p className="text-gray-600 dark:text-dark-text-secondary leading-relaxed">
              {slides[currentSlide].description}
            </p>
          </motion.div>
        </AnimatePresence>
      </div>

      {/* Bottom */}
      <div className="p-6 pb-10">
        {/* Dots */}
        <div className="flex justify-center gap-2 mb-6">
          {slides.map((_, index) => (
            <button
              key={index}
              onClick={() => setCurrentSlide(index)}
              className={`w-2 h-2 rounded-full transition-all ${
                index === currentSlide
                  ? 'w-6 bg-primary-500'
                  : 'bg-gray-300 dark:bg-dark-border'
              }`}
            />
          ))}
        </div>

        {/* Buttons */}
        <div className="flex gap-3">
          {currentSlide > 0 && (
            <Button
              variant="secondary"
              onClick={() => setCurrentSlide(currentSlide - 1)}
              className="flex-1"
            >
              이전
            </Button>
          )}
          <Button
            variant="primary"
            onClick={handleNext}
            rightIcon={<ChevronRight className="w-5 h-5" />}
            className={currentSlide === 0 ? 'w-full' : 'flex-1'}
          >
            {currentSlide === slides.length - 1 ? '시작하기' : '다음'}
          </Button>
        </div>
      </div>
    </div>
  );
}
