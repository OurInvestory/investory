package com.investory.backend.domain.stock.controller;

import com.investory.backend.domain.stock.dto.StockRequest;
import com.investory.backend.domain.stock.dto.StockResponse;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.stock.service.StockService;
import com.investory.backend.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "주식", description = "주식 관련 API")
@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "전체 종목 조회", description = "전체 종목 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StockResponse.Summary>>> getAllStocks(
            @RequestParam(required = false) Stock.Market market) {
        List<StockResponse.Summary> stocks = stockService.getAllStocks(market);
        return ResponseEntity.ok(ApiResponse.success(stocks));
    }

    @Operation(summary = "종목 상세 조회", description = "종목 상세 정보를 조회합니다.")
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<StockResponse.Detail>> getStockDetail(@PathVariable String code) {
        StockResponse.Detail stock = stockService.getStockDetail(code);
        return ResponseEntity.ok(ApiResponse.success(stock));
    }

    @Operation(summary = "종목 검색", description = "키워드로 종목을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<StockResponse.Summary>>> searchStocks(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<StockResponse.Summary> stocks = stockService.searchStocks(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(stocks));
    }

    @Operation(summary = "인기 종목 조회", description = "거래량/상승률/하락률 기준 인기 종목을 조회합니다.")
    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<StockResponse.Summary>>> getTopStocks(
            @RequestParam(defaultValue = "volume") String type,
            @RequestParam(defaultValue = "10") int limit) {
        List<StockResponse.Summary> stocks = stockService.getTopStocks(type, limit);
        return ResponseEntity.ok(ApiResponse.success(stocks));
    }

    @Operation(summary = "섹터 목록 조회", description = "모든 섹터 목록을 조회합니다.")
    @GetMapping("/sectors")
    public ResponseEntity<ApiResponse<List<String>>> getAllSectors() {
        List<String> sectors = stockService.getAllSectors();
        return ResponseEntity.ok(ApiResponse.success(sectors));
    }

    @Operation(summary = "섹터별 종목 조회", description = "특정 섹터의 종목을 조회합니다.")
    @GetMapping("/sectors/{sector}")
    public ResponseEntity<ApiResponse<List<StockResponse.Summary>>> getStocksBySector(@PathVariable String sector) {
        List<StockResponse.Summary> stocks = stockService.getStocksBySector(sector);
        return ResponseEntity.ok(ApiResponse.success(stocks));
    }

    @Operation(summary = "호가 조회", description = "종목의 호가 정보를 조회합니다.")
    @GetMapping("/{code}/orderbook")
    public ResponseEntity<ApiResponse<StockResponse.Orderbook>> getOrderbook(@PathVariable String code) {
        StockResponse.Orderbook orderbook = stockService.getOrderbook(code);
        return ResponseEntity.ok(ApiResponse.success(orderbook));
    }

    @Operation(summary = "관심 종목 조회", description = "내 관심 종목 목록을 조회합니다.")
    @GetMapping("/watchlist")
    public ResponseEntity<ApiResponse<List<StockResponse.WatchlistItem>>> getWatchlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String group) {
        List<StockResponse.WatchlistItem> watchlist = group != null
                ? stockService.getWatchlistByGroup(userDetails.getUsername(), group)
                : stockService.getWatchlist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(watchlist));
    }

    @Operation(summary = "관심 종목 그룹 조회", description = "관심 종목 그룹 목록을 조회합니다.")
    @GetMapping("/watchlist/groups")
    public ResponseEntity<ApiResponse<List<String>>> getWatchlistGroups(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<String> groups = stockService.getWatchlistGroups(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    @Operation(summary = "관심 종목 추가", description = "관심 종목을 추가합니다.")
    @PostMapping("/watchlist")
    public ResponseEntity<ApiResponse<StockResponse.WatchlistItem>> addToWatchlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody StockRequest.AddWatchlist request) {
        StockResponse.WatchlistItem item = stockService.addToWatchlist(
                userDetails.getUsername(), request.getStockCode(), request.getGroupName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("관심종목 추가 완료", item));
    }

    @Operation(summary = "관심 종목 삭제", description = "관심 종목을 삭제합니다.")
    @DeleteMapping("/watchlist/{code}")
    public ResponseEntity<ApiResponse<Void>> removeFromWatchlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String code) {
        stockService.removeFromWatchlist(userDetails.getUsername(), code);
        return ResponseEntity.ok(ApiResponse.success("관심종목 삭제 완료", null));
    }

    @Operation(summary = "관심 종목 여부 확인", description = "특정 종목이 관심종목인지 확인합니다.")
    @GetMapping("/watchlist/check/{code}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> isInWatchlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String code) {
        boolean inWatchlist = stockService.isInWatchlist(userDetails.getUsername(), code);
        return ResponseEntity.ok(ApiResponse.success(Map.of("inWatchlist", inWatchlist)));
    }
}
