package com.investory.backend.domain.wmti.service;

import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import com.investory.backend.domain.wmti.dto.WmtiRequest;
import com.investory.backend.domain.wmti.dto.WmtiResponse;
import com.investory.backend.domain.wmti.entity.WmtiResult;
import com.investory.backend.domain.wmti.repository.WmtiResultRepository;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WmtiService {

    private final WmtiResultRepository wmtiResultRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // 질문 목록 (하드코딩, 추후 DB로 이동 가능)
    private static final List<WmtiResponse.Question> QUESTIONS = List.of(
            WmtiResponse.Question.builder()
                    .id(1)
                    .question("투자 시 가장 중요하게 생각하는 것은?")
                    .options(List.of(
                            WmtiResponse.Option.builder().id(1).text("원금 보존").build(),
                            WmtiResponse.Option.builder().id(2).text("안정적인 수익").build(),
                            WmtiResponse.Option.builder().id(3).text("적당한 위험과 수익").build(),
                            WmtiResponse.Option.builder().id(4).text("높은 수익 추구").build(),
                            WmtiResponse.Option.builder().id(5).text("최대한 높은 수익").build()
                    ))
                    .build(),
            WmtiResponse.Question.builder()
                    .id(2)
                    .question("투자 손실이 발생했을 때 어떻게 대응하시겠습니까?")
                    .options(List.of(
                            WmtiResponse.Option.builder().id(1).text("즉시 손절하고 안전자산으로 이동").build(),
                            WmtiResponse.Option.builder().id(2).text("손실 규모를 보고 신중하게 결정").build(),
                            WmtiResponse.Option.builder().id(3).text("장기적 관점에서 보유 지속").build(),
                            WmtiResponse.Option.builder().id(4).text("추가 매수로 평균단가 낮추기").build(),
                            WmtiResponse.Option.builder().id(5).text("공격적으로 물타기").build()
                    ))
                    .build(),
            WmtiResponse.Question.builder()
                    .id(3)
                    .question("선호하는 투자 기간은?")
                    .options(List.of(
                            WmtiResponse.Option.builder().id(1).text("1년 미만 (단기)").build(),
                            WmtiResponse.Option.builder().id(2).text("1~3년 (중기)").build(),
                            WmtiResponse.Option.builder().id(3).text("3~5년 (중장기)").build(),
                            WmtiResponse.Option.builder().id(4).text("5~10년 (장기)").build(),
                            WmtiResponse.Option.builder().id(5).text("10년 이상 (초장기)").build()
                    ))
                    .build(),
            WmtiResponse.Question.builder()
                    .id(4)
                    .question("투자 가능 금액 대비 얼마나 투자하시겠습니까?")
                    .options(List.of(
                            WmtiResponse.Option.builder().id(1).text("20% 이하").build(),
                            WmtiResponse.Option.builder().id(2).text("20~40%").build(),
                            WmtiResponse.Option.builder().id(3).text("40~60%").build(),
                            WmtiResponse.Option.builder().id(4).text("60~80%").build(),
                            WmtiResponse.Option.builder().id(5).text("80% 이상").build()
                    ))
                    .build(),
            WmtiResponse.Question.builder()
                    .id(5)
                    .question("투자 경험은 어느 정도입니까?")
                    .options(List.of(
                            WmtiResponse.Option.builder().id(1).text("투자 경험 없음").build(),
                            WmtiResponse.Option.builder().id(2).text("예적금 위주").build(),
                            WmtiResponse.Option.builder().id(3).text("펀드/ETF 투자 경험").build(),
                            WmtiResponse.Option.builder().id(4).text("주식 직접 투자 경험").build(),
                            WmtiResponse.Option.builder().id(5).text("파생상품 투자 경험").build()
                    ))
                    .build()
    );

    public WmtiResponse.QuestionSet getQuestions() {
        return WmtiResponse.QuestionSet.builder()
                .questions(QUESTIONS)
                .totalQuestions(QUESTIONS.size())
                .build();
    }

    @Transactional
    public WmtiResponse.Result submitTest(String loginId, WmtiRequest.Submit request) {
        User user = getUserByLoginId(loginId);

        // 점수 계산
        int stabilityScore = 0;
        int growthScore = 0;
        int riskScore = 0;
        int incomeScore = 0;

        for (WmtiRequest.Answer answer : request.getAnswers()) {
            int optionId = answer.getOptionId();
            
            // 각 질문별로 점수 계산 (옵션 ID가 높을수록 공격적)
            switch (answer.getQuestionId()) {
                case 1 -> { // 투자 시 중요한 것
                    stabilityScore += (6 - optionId) * 4;
                    riskScore += optionId * 4;
                }
                case 2 -> { // 손실 대응
                    stabilityScore += (6 - optionId) * 4;
                    riskScore += optionId * 4;
                }
                case 3 -> { // 투자 기간
                    growthScore += optionId * 4;
                }
                case 4 -> { // 투자 비율
                    riskScore += optionId * 4;
                }
                case 5 -> { // 투자 경험
                    riskScore += optionId * 3;
                    growthScore += optionId * 2;
                }
            }
        }

        // 유형 결정
        int totalRiskScore = riskScore;
        String resultType;
        if (totalRiskScore <= 30) {
            resultType = "WMTI-C"; // Conservative
        } else if (totalRiskScore <= 50) {
            resultType = "WMTI-M"; // Moderate
        } else {
            resultType = "WMTI-A"; // Aggressive
        }

        // 답변 JSON 저장
        String answersJson;
        try {
            answersJson = objectMapper.writeValueAsString(request.getAnswers());
        } catch (JsonProcessingException e) {
            answersJson = "[]";
        }

        // 결과 저장
        WmtiResult wmtiResult = WmtiResult.create(
                user, resultType, stabilityScore, growthScore, riskScore, incomeScore, answersJson
        );
        WmtiResult savedResult = wmtiResultRepository.save(wmtiResult);

        // 사용자 WMTI 타입 업데이트
        user.updateWmtiType(resultType);
        user.addExperience(100); // WMTI 테스트 완료 경험치
        userRepository.save(user);

        log.info("WMTI 테스트 완료: {} - {}", loginId, resultType);

        return WmtiResponse.Result.from(savedResult);
    }

    @Transactional(readOnly = true)
    public WmtiResponse.Result getLatestResult(String loginId) {
        User user = getUserByLoginId(loginId);
        WmtiResult result = wmtiResultRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.WMTI_NOT_COMPLETED));
        return WmtiResponse.Result.from(result);
    }

    @Transactional(readOnly = true)
    public List<WmtiResponse.Result> getAllResults(String loginId) {
        User user = getUserByLoginId(loginId);
        return wmtiResultRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(WmtiResponse.Result::from)
                .collect(Collectors.toList());
    }

    private User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
