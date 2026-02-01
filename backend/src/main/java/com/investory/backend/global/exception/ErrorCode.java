package com.investory.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C003", "잘못된 타입입니다."),
    
    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "만료된 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A005", "아이디 또는 비밀번호가 일치하지 않습니다."),
    
    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "U002", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "U004", "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U005", "비밀번호가 일치하지 않습니다."),
    
    // Email
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "이메일 발송에 실패했습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "E002", "인증 코드가 일치하지 않습니다."),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "E003", "만료된 인증 코드입니다."),
    
    // Stock
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "종목을 찾을 수 없습니다."),
    DUPLICATE_WATCHLIST(HttpStatus.CONFLICT, "S002", "이미 관심 종목에 등록되어 있습니다."),
    
    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "O002", "취소할 수 없는 주문입니다."),
    INVALID_ORDER_PRICE(HttpStatus.BAD_REQUEST, "O003", "유효하지 않은 주문 가격입니다."),
    INSUFFICIENT_HOLDING(HttpStatus.BAD_REQUEST, "O004", "보유 수량이 부족합니다."),
    
    // Holding
    HOLDING_NOT_FOUND(HttpStatus.NOT_FOUND, "H001", "보유 종목을 찾을 수 없습니다."),
    
    // WMTI
    WMTI_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "W001", "투자 성향 분석이 완료되지 않았습니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
}
