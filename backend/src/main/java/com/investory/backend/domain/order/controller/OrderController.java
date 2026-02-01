package com.investory.backend.domain.order.controller;

import com.investory.backend.domain.order.dto.OrderRequest;
import com.investory.backend.domain.order.dto.OrderResponse;
import com.investory.backend.domain.order.service.OrderService;
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

@Tag(name = "주문", description = "주문 관련 API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "매수/매도 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse.Detail>> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderRequest.Create request) {
        OrderResponse.Detail order = orderService.createOrder(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("주문이 접수되었습니다.", order));
    }

    @Operation(summary = "주문 취소", description = "대기 중인 주문을 취소합니다.")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse.Detail>> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @RequestBody(required = false) OrderRequest.Cancel request) {
        String reason = request != null ? request.getReason() : null;
        OrderResponse.Detail order = orderService.cancelOrder(userDetails.getUsername(), orderId, reason);
        return ResponseEntity.ok(ApiResponse.success("주문이 취소되었습니다.", order));
    }

    @Operation(summary = "주문 내역 조회", description = "전체 주문 내역을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse.Summary>>> getOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OrderResponse.Summary> orders = orderService.getOrders(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse.Detail>> getOrderDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        OrderResponse.Detail order = orderService.getOrderDetail(userDetails.getUsername(), orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @Operation(summary = "대기 중 주문 조회", description = "대기 중인 주문 목록을 조회합니다.")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<OrderResponse.Summary>>> getPendingOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<OrderResponse.Summary> orders = orderService.getPendingOrders(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}
