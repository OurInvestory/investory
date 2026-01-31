package com.investory.backend.global.exception.dto;

import com.investory.backend.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ErrorResponse {
    
    private final boolean success;
    private final String code;
    private final String message;
    private final List<FieldError> errors;
    private final LocalDateTime timestamp;
    
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return ErrorResponse.builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(FieldError.of(bindingResult))
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;
        
        public static List<FieldError> of(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> FieldError.builder()
                            .field(error.getField())
                            .value(error.getRejectedValue() != null ? 
                                    error.getRejectedValue().toString() : "")
                            .reason(error.getDefaultMessage())
                            .build())
                    .collect(Collectors.toList());
        }
    }
}
