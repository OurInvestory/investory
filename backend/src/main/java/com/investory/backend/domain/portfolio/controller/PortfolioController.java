package com.investory.backend.domain.portfolio.controller;

import com.investory.backend.domain.portfolio.dto.PortfolioResponse;
import com.investory.backend.domain.portfolio.service.PortfolioService;
import com.investory.backend.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "포트폴리오", description = "포트폴리오 관련 API")
@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "포트폴리오 조회", description = "내 포트폴리오를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PortfolioResponse.Summary>> getPortfolio(
            @AuthenticationPrincipal UserDetails userDetails) {
        PortfolioResponse.Summary portfolio = portfolioService.getPortfolio(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(portfolio));
    }

    @Operation(summary = "시장별 포트폴리오 조회", description = "국내/해외 시장별 포트폴리오를 조회합니다.")
    @GetMapping("/{marketType}")
    public ResponseEntity<ApiResponse<PortfolioResponse.Summary>> getPortfolioByMarket(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String marketType) {
        PortfolioResponse.Summary portfolio = portfolioService.getPortfolioByMarket(userDetails.getUsername(), marketType);
        return ResponseEntity.ok(ApiResponse.success(portfolio));
    }
}
