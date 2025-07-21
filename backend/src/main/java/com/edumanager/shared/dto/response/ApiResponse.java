package com.edumanager.shared.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * API 응답을 위한 공통 래퍼 클래스
 * Java 21 Record와 Generic을 활용한 타입 안전성 보장
 * 
 * @param <T> 응답 데이터의 타입
 */
@Getter
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private final boolean success;
    private final String message;
    private final T data;
    private final ErrorResponse error;
    private final LocalDateTime timestamp;
    
    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다.")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 성공 응답 생성 (데이터와 메시지 포함)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return ApiResponse.<T>builder()
                .success(false)
                .message("요청 처리 중 오류가 발생했습니다.")
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 실패 응답 생성 (메시지 커스텀)
     */
    public static <T> ApiResponse<T> error(ErrorResponse error, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 페이징 성공 응답 생성
     */
    public static <T> ApiResponse<PageResponse<T>> success(PageResponse<T> pageData) {
        return ApiResponse.<PageResponse<T>>builder()
                .success(true)
                .message("페이징 데이터를 성공적으로 조회했습니다.")
                .data(pageData)
                .timestamp(LocalDateTime.now())
                .build();
    }
}