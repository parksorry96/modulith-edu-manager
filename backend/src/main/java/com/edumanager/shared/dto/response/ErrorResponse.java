package com.edumanager.shared.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 에러 응답을 위한 데이터 클래스
 * Java 21과 Spring Boot 3.5 최신 기능 활용
 */
@Getter
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;
    private final String path;
    private final String category;
    private final String severity;
    private final List<String> details;
    private final String traceId; // 분산 추적을 위한 ID (추후 구현)
    
    /**
     * 간단한 에러 응답 생성
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 상세 에러 응답 생성
     */
    public static ErrorResponse of(String code, String message, String path, List<String> details) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .path(path)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
    }
}