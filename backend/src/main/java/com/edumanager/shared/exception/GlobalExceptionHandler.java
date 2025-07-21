package com.edumanager.shared.exception;

import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.shared.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리기 - Spring Boot 3.5 + Java 21 최신 기능 활용
 * 
 * 주요 기능:
 * 1. BusinessException 처리
 * 2. Validation 예외 처리
 * 3. Security 예외 처리
 * 4. 일반적인 런타임 예외 처리
 * 5. 구조화된 에러 응답
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, 
            HttpServletRequest request) {
        
        log.warn("Business Exception: {} - {}", ex.getErrorCode().getCode(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .category(ex.getErrorCode().getCategory().name())
                .severity(ex.getErrorCode().getSeverity().name())
                .build();
        
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ApiResponse.error(errorResponse));
    }
    
    /**
     * Bean Validation 예외 처리 (@Valid, @Validated)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());
        
        log.warn("Validation failed: {}", errors);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_FAILED.getCode())
                .message("입력 데이터 검증에 실패했습니다: " + String.join(", ", errors))
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .category(ErrorCode.ErrorCategory.VALIDATION.name())
                .severity(ErrorCode.ErrorSeverity.LOW.name())
                .details(errors)
                .build();
        
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(errorResponse));
    }
    
    /**
     * BindException 처리 (폼 데이터 바인딩 오류)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(
            BindException ex,
            HttpServletRequest request) {
        
        List<String> errors = ex.getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());
        
        log.warn("Bind exception: {}", errors);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.INVALID_PARAMETER.getCode())
                .message("파라미터 바인딩에 실패했습니다: " + String.join(", ", errors))
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .category(ErrorCode.ErrorCategory.VALIDATION.name())
                .severity(ErrorCode.ErrorSeverity.LOW.name())
                .details(errors)
                .build();
        
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(errorResponse));
    }
    
    /**
     * ConstraintViolationException 처리 (경로 변수, 요청 파라미터 검증)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.toList());
        
        log.warn("Constraint violation: {}", errors);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.INVALID_PARAMETER.getCode())
                .message("파라미터 제약 조건 위반: " + String.join(", ", errors))
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .category(ErrorCode.ErrorCategory.VALIDATION.name())
                .severity(ErrorCode.ErrorSeverity.LOW.name())
                .details(errors)
                .build();
        
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(errorResponse));
    }
    
    /**
     * Spring Security 인증 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        
        log.warn("Authentication failed: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message("인증에 실패했습니다.")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .category(ErrorCode.ErrorCategory.AUTH.name())
                .severity(ErrorCode.ErrorSeverity.MEDIUM.name())
                .build();
        
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(errorResponse));
    }
    
    /**
     * Spring Security 인가 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        
        log.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.ACCESS_DENIED.getCode())
                .message("접근이 거부되었습니다.")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .category(ErrorCode.ErrorCategory.AUTH.name())
                .severity(ErrorCode.ErrorSeverity.MEDIUM.name())
                .build();
        
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(errorResponse));
    }
    
    /**
     * 일반적인 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        
        log.error("Unexpected runtime exception", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message("서버 내부 오류가 발생했습니다.")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .category(ErrorCode.ErrorCategory.COMMON.name())
                .severity(ErrorCode.ErrorSeverity.HIGH.name())
                .build();
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(errorResponse));
    }
    
    /**
     * 최상위 Exception 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected exception", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message("예상치 못한 오류가 발생했습니다.")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .category(ErrorCode.ErrorCategory.COMMON.name())
                .severity(ErrorCode.ErrorSeverity.HIGH.name())
                .build();
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(errorResponse));
    }
    
    /**
     * Field Error 포맷팅 (Java 21 Text Block과 String formatting 활용)
     */
    private String formatFieldError(FieldError fieldError) {
        return """
            Field '%s': %s (rejected value: %s)
            """.formatted(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
        ).trim();
    }
    
    /**
     * Constraint Violation 포맷팅
     */
    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        return """
            Path '%s': %s (invalid value: %s)
            """.formatted(
                violation.getPropertyPath(),
                violation.getMessage(),
                violation.getInvalidValue()
        ).trim();
    }
}