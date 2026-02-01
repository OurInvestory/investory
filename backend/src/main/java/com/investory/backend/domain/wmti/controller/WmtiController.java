package com.investory.backend.domain.wmti.controller;

import com.investory.backend.domain.wmti.dto.WmtiRequest;
import com.investory.backend.domain.wmti.dto.WmtiResponse;
import com.investory.backend.domain.wmti.service.WmtiService;
import com.investory.backend.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WMTI", description = "투자 성향 분석 API")
@RestController
@RequestMapping("/wmti")
@RequiredArgsConstructor
public class WmtiController {

    private final WmtiService wmtiService;

    @Operation(summary = "WMTI 질문 조회", description = "투자 성향 분석 질문 목록을 조회합니다.")
    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<WmtiResponse.QuestionSet>> getQuestions() {
        WmtiResponse.QuestionSet questions = wmtiService.getQuestions();
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @Operation(summary = "WMTI 테스트 제출", description = "투자 성향 분석 테스트 결과를 제출합니다.")
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<WmtiResponse.Result>> submitTest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WmtiRequest.Submit request) {
        WmtiResponse.Result result = wmtiService.submitTest(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("투자 성향 분석 완료", result));
    }

    @Operation(summary = "최신 WMTI 결과 조회", description = "가장 최근 투자 성향 분석 결과를 조회합니다.")
    @GetMapping("/result")
    public ResponseEntity<ApiResponse<WmtiResponse.Result>> getLatestResult(
            @AuthenticationPrincipal UserDetails userDetails) {
        WmtiResponse.Result result = wmtiService.getLatestResult(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "모든 WMTI 결과 조회", description = "모든 투자 성향 분석 결과를 조회합니다.")
    @GetMapping("/results")
    public ResponseEntity<ApiResponse<List<WmtiResponse.Result>>> getAllResults(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<WmtiResponse.Result> results = wmtiService.getAllResults(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
