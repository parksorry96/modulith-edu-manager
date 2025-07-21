package com.edumanager.shared.exception;

import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 예외를 처리하는 클래스
 * Java 21의 Record와 Pattern Matching을 활용
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object[] args;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public BusinessException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(String.format(errorCode.getMessage(), args), cause);
        this.errorCode = errorCode;
        this.args = args;
    }
    
    /**
     * 에러 정보를 Record로 반환 (Java 21 Record 활용)
     */
    public ErrorInfo getErrorInfo() {
        return new ErrorInfo(
            errorCode.getCode(),
            errorCode.getMessage(),
            errorCode.getHttpStatus(),
            errorCode.getCategory(),
            errorCode.getSeverity()
        );
    }
    
    /**
     * 에러 정보를 담는 Record (Java 14+ Record 기능)
     */
    public record ErrorInfo(
        String code,
        String message,
        org.springframework.http.HttpStatus httpStatus,
        ErrorCode.ErrorCategory category,
        ErrorCode.ErrorSeverity severity
    ) {}
}