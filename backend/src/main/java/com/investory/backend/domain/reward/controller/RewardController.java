package com.investory.backend.domain.reward.controller;

import com.investory.backend.domain.reward.dto.RewardResponse;
import com.investory.backend.domain.reward.service.RewardService;
import com.investory.backend.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리워드", description = "레벨 및 업적 관련 API")
@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @Operation(summary = "레벨 정보 조회", description = "내 레벨 및 경험치 정보를 조회합니다.")
    @GetMapping("/level")
    public ResponseEntity<ApiResponse<RewardResponse.LevelInfo>> getLevelInfo(
            @AuthenticationPrincipal UserDetails userDetails) {
        RewardResponse.LevelInfo levelInfo = rewardService.getLevelInfo(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(levelInfo));
    }

    @Operation(summary = "업적 목록 조회", description = "모든 업적 및 달성 현황을 조회합니다.")
    @GetMapping("/achievements")
    public ResponseEntity<ApiResponse<RewardResponse.AchievementSummary>> getAchievements(
            @AuthenticationPrincipal UserDetails userDetails) {
        RewardResponse.AchievementSummary achievements = rewardService.getAchievements(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(achievements));
    }
}
