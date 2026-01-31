import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, CheckCircle } from 'lucide-react';
import { Button, Card, ProgressBar } from '@/components/common';
import { cn } from '@/utils';
import { motion, AnimatePresence } from 'framer-motion';

// WMTI 질문 목업 데이터
const wmtiQuestions = [
  {
    id: 1,
    question: '투자 손실이 발생했을 때 어떻게 대응하시나요?',
    description: '최대 1개를 선택해주세요.',
    options: [
      { id: 1, text: '즉시 매도하여 추가 손실을 방지합니다', score: 1, description: '손절이 곧 본전입니다' },
      { id: 2, text: '불안하지만 조금 더 기다립니다', score: 2, description: '언젠간 오르겠지' },
      { id: 3, text: '시장 상황을 분석해서 판단합니다', score: 3, description: '차트와 뉴스를 확인합니다' },
      { id: 4, text: '오히려 추가 매수를 고려합니다', score: 4, description: '물타기? 불타기?' },
      { id: 5, text: '장기적 관점에서 묵묵히 보유합니다', score: 5, description: '시간이 약이다' },
    ],
  },
  {
    id: 2,
    question: '투자 손실이 발생했을 때 어떻게 대응하시나요?',
    description: '최대 1개를 선택해주세요.',
    options: [
      { id: 1, text: '평일 천천히 가격 움직합니다', score: 1, description: '손실 최소 무브입니다' },
      { id: 2, text: '경험적인 수기를 훔칩니다', score: 2, description: '무난 한 정면 무난' },
      { id: 3, text: '시장 상황을 분석해서 판단합니다', score: 3, description: '직접으로 미래이익을 봅니다' },
      { id: 4, text: '장기적 관점에서 매수를 구매합 니다', score: 4, description: '기회일 수 있으니까요' },
      { id: 5, text: '실리적 주가 매수 지출을 줄입니다', score: 5, description: '토틀 약 다이' },
    ],
  },
  {
    id: 3,
    question: '새로운 투자 기회가 생겼을 때 어떻게 접근하시나요?',
    description: '최대 1개를 선택해주세요.',
    options: [
      { id: 1, text: '충분한 정보 수집 후 신중하게 투자합니다', score: 1 },
      { id: 2, text: '전문가 의견을 참고하여 결정합니다', score: 2 },
      { id: 3, text: '소액으로 먼저 테스트해봅니다', score: 3 },
      { id: 4, text: '직감을 믿고 빠르게 진입합니다', score: 4 },
      { id: 5, text: '기회를 놓치지 않기 위해 과감하게 투자합니다', score: 5 },
    ],
  },
];

const wmtiTypes = {
  conservative: {
    type: 'WMTI-C',
    title: '안정 추구형',
    description: '리스크를 최소화하고 안정적인 수익을 추구하는 투자자입니다.',
    traits: ['신중한 판단', '장기 투자 선호', '분산 투자'],
    recommendations: ['국채', '우량주', '배당주'],
  },
  moderate: {
    type: 'WMTI-M',
    title: '균형 투자형',
    description: '적절한 리스크와 수익의 균형을 추구하는 투자자입니다.',
    traits: ['분석적 접근', '균형 잡힌 포트폴리오', '중기 투자'],
    recommendations: ['ETF', '대형주', '성장주'],
  },
  aggressive: {
    type: 'WMTI-A',
    title: '적극 투자형',
    description: '높은 수익을 위해 리스크를 감수하는 공격적인 투자자입니다.',
    traits: ['빠른 의사결정', '트렌드 추종', '단기 투자'],
    recommendations: ['성장주', '기술주', '해외주식'],
  },
};

export default function WMTIPage() {
  const navigate = useNavigate();
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState<number[]>([]);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [isComplete, setIsComplete] = useState(false);
  const [result, setResult] = useState<typeof wmtiTypes.conservative | null>(null);

  const totalQuestions = wmtiQuestions.length;
  const progress = ((currentQuestion + 1) / totalQuestions) * 100;

  const handleOptionSelect = (optionId: number) => {
    setSelectedOption(optionId);
  };

  const handleNext = () => {
    if (selectedOption === null) return;

    const newAnswers = [...answers, selectedOption];
    setAnswers(newAnswers);

    if (currentQuestion < totalQuestions - 1) {
      setCurrentQuestion(currentQuestion + 1);
      setSelectedOption(null);
    } else {
      // 결과 계산
      const avgScore = newAnswers.reduce((a, b) => a + b, 0) / newAnswers.length;
      if (avgScore <= 2) {
        setResult(wmtiTypes.conservative);
      } else if (avgScore <= 3.5) {
        setResult(wmtiTypes.moderate);
      } else {
        setResult(wmtiTypes.aggressive);
      }
      setIsComplete(true);
    }
  };

  const question = wmtiQuestions[currentQuestion];

  // 결과 화면
  if (isComplete && result) {
    return (
      <div className="min-h-screen bg-white dark:bg-dark-bg-primary">
        <div className="sticky top-0 bg-white dark:bg-dark-bg-primary z-10">
          <div className="flex items-center px-4 h-14">
            <button onClick={() => navigate('/')} className="p-2 -ml-2">
              <ChevronLeft className="w-6 h-6 text-gray-700 dark:text-dark-text-primary" />
            </button>
          </div>
        </div>

        <div className="px-6 py-8">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-center mb-8"
          >
            <div className="w-20 h-20 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <CheckCircle className="w-10 h-10 text-primary-500" />
            </div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-dark-text-primary mb-2">
              투자 성향 분석 완료!
            </h1>
            <p className="text-gray-500 dark:text-dark-text-secondary">
              당신의 투자 유형이 분석되었습니다
            </p>
          </motion.div>

          <Card className="p-6 mb-6 text-center">
            <div className="w-24 h-24 bg-primary-500 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-3xl font-bold text-white">{result.type}</span>
            </div>
            <h2 className="text-xl font-bold text-gray-900 dark:text-dark-text-primary mb-2">
              {result.title}
            </h2>
            <p className="text-gray-600 dark:text-dark-text-secondary mb-6">
              {result.description}
            </p>

            <div className="text-left">
              <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-2">투자 특성</h3>
              <div className="flex flex-wrap gap-2 mb-4">
                {result.traits.map((trait) => (
                  <span
                    key={trait}
                    className="px-3 py-1 bg-primary-100 dark:bg-primary-900/30 text-primary-700 dark:text-primary-300 rounded-full text-sm"
                  >
                    {trait}
                  </span>
                ))}
              </div>

              <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-2">추천 투자 상품</h3>
              <div className="flex flex-wrap gap-2">
                {result.recommendations.map((rec) => (
                  <span
                    key={rec}
                    className="px-3 py-1 bg-gray-100 dark:bg-dark-bg-secondary text-gray-700 dark:text-dark-text-primary rounded-full text-sm"
                  >
                    {rec}
                  </span>
                ))}
              </div>
            </div>
          </Card>

          <div className="space-y-3">
            <Button variant="primary" fullWidth onClick={() => navigate('/products')}>
              추천 상품 보러가기
            </Button>
            <Button variant="secondary" fullWidth onClick={() => navigate('/')}>
              홈으로 돌아가기
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // 질문 화면
  return (
    <div className="min-h-screen bg-white dark:bg-dark-bg-primary">
      {/* Header */}
      <div className="sticky top-0 bg-white dark:bg-dark-bg-primary z-10">
        <div className="flex items-center justify-between px-4 h-14">
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 bg-primary-500 rounded flex items-center justify-center">
              <span className="text-white font-bold text-xs">W</span>
            </div>
            <span className="font-bold text-primary-500">Investory</span>
          </div>
          <span className="text-sm text-primary-500">
            {currentQuestion + 1}/{totalQuestions}
          </span>
        </div>
        <div className="px-4 pb-2">
          <ProgressBar value={progress} max={100} color="primary" />
        </div>
      </div>

      <div className="px-6 py-6">
        {/* 질문 헤더 */}
        <div className="mb-8">
          <h1 className="text-xl font-bold text-gray-900 dark:text-dark-text-primary mb-2">
            투자 성향 진단
          </h1>
          <p className="text-gray-500 dark:text-dark-text-secondary">
            나만의 투자 스타일을 알아보세요!
          </p>
        </div>

        {/* 질문 */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestion}
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            transition={{ duration: 0.3 }}
          >
            <div className="mb-6">
              <div className="flex items-center gap-2 mb-3">
                <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
                  <span className="text-primary-600 font-semibold">{currentQuestion + 1}</span>
                </div>
              </div>
              <h2 className="text-lg font-semibold text-gray-900 dark:text-dark-text-primary mb-1">
                {question.question}
              </h2>
              <p className="text-sm text-gray-500 dark:text-dark-text-secondary">
                {question.description}
              </p>
            </div>

            {/* 옵션들 */}
            <div className="space-y-3 mb-8">
              {question.options.map((option) => (
                <button
                  key={option.id}
                  onClick={() => handleOptionSelect(option.score)}
                  className={cn(
                    'w-full p-4 rounded-xl border-2 text-left transition-all',
                    selectedOption === option.score
                      ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20'
                      : 'border-gray-200 dark:border-dark-border hover:border-gray-300 dark:hover:border-gray-600'
                  )}
                >
                  <div className="flex items-start gap-3">
                    <div
                      className={cn(
                        'w-5 h-5 rounded-full border-2 flex-shrink-0 mt-0.5 flex items-center justify-center',
                        selectedOption === option.score
                          ? 'border-primary-500 bg-primary-500'
                          : 'border-gray-300 dark:border-dark-border'
                      )}
                    >
                      {selectedOption === option.score && (
                        <div className="w-2 h-2 bg-white rounded-full" />
                      )}
                    </div>
                    <div>
                      <p className={cn(
                        'font-medium',
                        selectedOption === option.score
                          ? 'text-primary-700 dark:text-primary-300'
                          : 'text-gray-900 dark:text-dark-text-primary'
                      )}>
                        {option.text}
                      </p>
                      {option.description && (
                        <p className="text-sm text-gray-500 dark:text-dark-text-secondary mt-1">
                          {option.description}
                        </p>
                      )}
                    </div>
                  </div>
                </button>
              ))}
            </div>
          </motion.div>
        </AnimatePresence>

        {/* 다음 버튼 */}
        <Button
          variant="primary"
          fullWidth
          onClick={handleNext}
          disabled={selectedOption === null}
        >
          {currentQuestion === totalQuestions - 1 ? '결과 보기' : '다음 질문'}
        </Button>
      </div>
    </div>
  );
}
