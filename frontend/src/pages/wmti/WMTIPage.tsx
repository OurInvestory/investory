import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, CheckCircle } from 'lucide-react';
import { Button, Card, ProgressBar } from '@/components/common';
import { cn } from '@/utils';
import { motion, AnimatePresence } from 'framer-motion';
import { useWmtiQuestions, useWmtiResult, useSubmitWmti } from '@/hooks/useApi';

const wmtiTypes: Record<string, { type: string; title: string; description: string; traits: string[]; recommendations: string[] }> = {
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
  const [answers, setAnswers] = useState<{ questionId: number; value: number }[]>([]);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [isComplete, setIsComplete] = useState(false);
  const [result, setResult] = useState<(typeof wmtiTypes)['conservative'] | null>(null);

  const { data: questionsData } = useWmtiQuestions();
  const { data: existingResult } = useWmtiResult();
  const submitWmti = useSubmitWmti();

  const questions = questionsData || [];
  const totalQuestions = questions.length || 1;
  const progress = ((currentQuestion + 1) / totalQuestions) * 100;

  // If user already has a result, show it
  if (existingResult && !isComplete && answers.length === 0) {
    const typeKey = existingResult.type?.toLowerCase().includes('aggressive') ? 'aggressive'
      : existingResult.type?.toLowerCase().includes('moderate') ? 'moderate' : 'conservative';
    const displayResult = wmtiTypes[typeKey] || {
      type: existingResult.type,
      title: existingResult.typeName,
      description: existingResult.typeDescription,
      traits: Object.keys(existingResult.scores || {}),
      recommendations: [],
    };

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
          <div className="text-center mb-8">
            <div className="w-20 h-20 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <CheckCircle className="w-10 h-10 text-primary-500" />
            </div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-dark-text-primary mb-2">
              당신의 투자 유형
            </h1>
          </div>

          <Card className="p-6 mb-6 text-center">
            <div className="w-24 h-24 bg-primary-500 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-2xl font-bold text-white">{displayResult.type}</span>
            </div>
            <h2 className="text-xl font-bold text-gray-900 dark:text-dark-text-primary mb-2">
              {displayResult.title}
            </h2>
            <p className="text-gray-600 dark:text-dark-text-secondary mb-6">
              {displayResult.description}
            </p>
            <div className="text-left">
              <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-2">투자 특성</h3>
              <div className="flex flex-wrap gap-2 mb-4">
                {displayResult.traits.map((trait) => (
                  <span key={trait} className="px-3 py-1 bg-primary-100 dark:bg-primary-900/30 text-primary-700 dark:text-primary-300 rounded-full text-sm">
                    {trait}
                  </span>
                ))}
              </div>
              {displayResult.recommendations.length > 0 && (
                <>
                  <h3 className="font-semibold text-gray-900 dark:text-dark-text-primary mb-2">추천 투자 상품</h3>
                  <div className="flex flex-wrap gap-2">
                    {displayResult.recommendations.map((rec) => (
                      <span key={rec} className="px-3 py-1 bg-gray-100 dark:bg-dark-bg-secondary text-gray-700 dark:text-dark-text-primary rounded-full text-sm">
                        {rec}
                      </span>
                    ))}
                  </div>
                </>
              )}
            </div>
          </Card>
          <div className="space-y-3">
            <Button variant="primary" fullWidth onClick={() => { setAnswers([]); setIsComplete(false); setCurrentQuestion(0); }}>
              다시 진단하기
            </Button>
            <Button variant="secondary" fullWidth onClick={() => navigate('/')}>
              홈으로 돌아가기
            </Button>
          </div>
        </div>
      </div>
    );
  }

  const handleOptionSelect = (optionId: number) => {
    setSelectedOption(optionId);
  };

  const handleNext = async () => {
    if (selectedOption === null || questions.length === 0) return;

    const question = questions[currentQuestion];
    const newAnswers = [...answers, { questionId: question.id, value: selectedOption }];
    setAnswers(newAnswers);

    if (currentQuestion < totalQuestions - 1) {
      setCurrentQuestion(currentQuestion + 1);
      setSelectedOption(null);
    } else {
      // Submit to API
      try {
        const apiResult = await submitWmti.mutateAsync({ answers: newAnswers });
        const typeKey = apiResult.type?.toLowerCase().includes('aggressive') ? 'aggressive'
          : apiResult.type?.toLowerCase().includes('moderate') ? 'moderate' : 'conservative';
        setResult(wmtiTypes[typeKey] || {
          type: apiResult.type,
          title: apiResult.typeName,
          description: apiResult.typeDescription,
          traits: Object.keys(apiResult.scores || {}),
          recommendations: [],
        });
      } catch {
        // Fallback: calculate locally
        const avgScore = newAnswers.reduce((a, b) => a + b.value, 0) / newAnswers.length;
        if (avgScore <= 2) setResult(wmtiTypes.conservative);
        else if (avgScore <= 3.5) setResult(wmtiTypes.moderate);
        else setResult(wmtiTypes.aggressive);
      }
      setIsComplete(true);
    }
  };

  if (questions.length === 0) {
    return (
      <div className="min-h-screen bg-white dark:bg-dark-bg-primary flex items-center justify-center">
        <p className="text-gray-500">질문을 불러오는 중...</p>
      </div>
    );
  }

  const question = questions[currentQuestion];

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
              {(question.options || []).map((option: any, idx: number) => (
                <button
                  key={idx}
                  onClick={() => handleOptionSelect(option.value)}
                  className={cn(
                    'w-full p-4 rounded-xl border-2 text-left transition-all',
                    selectedOption === option.value
                      ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20'
                      : 'border-gray-200 dark:border-dark-border hover:border-gray-300 dark:hover:border-gray-600'
                  )}
                >
                  <div className="flex items-start gap-3">
                    <div
                      className={cn(
                        'w-5 h-5 rounded-full border-2 flex-shrink-0 mt-0.5 flex items-center justify-center',
                        selectedOption === option.value
                          ? 'border-primary-500 bg-primary-500'
                          : 'border-gray-300 dark:border-dark-border'
                      )}
                    >
                      {selectedOption === option.value && (
                        <div className="w-2 h-2 bg-white rounded-full" />
                      )}
                    </div>
                    <div>
                      <p className={cn(
                        'font-medium',
                        selectedOption === option.value
                          ? 'text-primary-700 dark:text-primary-300'
                          : 'text-gray-900 dark:text-dark-text-primary'
                      )}>
                        {option.label}
                      </p>
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
