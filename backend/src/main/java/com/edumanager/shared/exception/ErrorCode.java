package com.edumanager.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 시스템 전반의 에러 코드를 관리하는 enum
 * Java 21의 최신 기능들을 활용하여 구현
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // 공통 에러 (1000~1999)
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청입니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "C002", "유효하지 않은 파라미터입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "내부 서버 오류가 발생했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C005", "접근이 거부되었습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C006", "인증이 필요합니다."),
    
    // 사용자 관련 에러 (2000~2999)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 일치하지 않습니다."),
    USER_ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "U004", "사용자 계정이 잠겨있습니다."),
    USER_ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "U005", "사용자 계정이 비활성화되었습니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "U006", "유효하지 않은 사용자 권한입니다."),
    
    // 인증 관련 에러 (3000~3999)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A003", "토큰을 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "유효하지 않은 리프레시 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A005", "로그인에 실패했습니다."),
    
    // 학생 관련 에러 (4000~4999)
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "학생을 찾을 수 없습니다."),
    STUDENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "S002", "이미 존재하는 학생입니다."),
    INVALID_ENROLLMENT_DATE(HttpStatus.BAD_REQUEST, "S003", "유효하지 않은 입학일입니다."),
    STUDENT_ALREADY_ENROLLED(HttpStatus.CONFLICT, "S004", "이미 등록된 학생입니다."),
    
    // 강의 관련 에러 (5000~5999)
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "강의를 찾을 수 없습니다."),
    COURSE_FULL(HttpStatus.CONFLICT, "C002", "강의 정원이 가득찼습니다."),
    INVALID_COURSE_DATE(HttpStatus.BAD_REQUEST, "C003", "유효하지 않은 강의 일정입니다."),
    
    // 검증 관련 에러 (6000~6999)
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "V001", "입력 데이터 검증에 실패했습니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "V002", "필수 입력 필드가 누락되었습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "V003", "유효하지 않은 이메일 형식입니다."),
    INVALID_PHONE_FORMAT(HttpStatus.BAD_REQUEST, "V004", "유효하지 않은 전화번호 형식입니다."),
    
    // 외부 시스템 연동 에러 (7000~7999)
    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E001", "외부 API 호출에 실패했습니다."),
    KAFKA_PUBLISH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "이벤트 발행에 실패했습니다."),
    REDIS_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E003", "Redis 연결에 실패했습니다."),
    
    // 파일 관련 에러 (8000~8999)
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "파일을 찾을 수 없습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F002", "파일 업로드에 실패했습니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "F003", "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "F004", "파일 크기가 제한을 초과했습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    
    /**
     * Java 21의 Pattern Matching을 활용한 에러 코드 분류
     */
    public ErrorCategory getCategory() {
        return switch (code.charAt(0)) {
            case 'C' -> ErrorCategory.COMMON;
            case 'U' -> ErrorCategory.USER;
            case 'A' -> ErrorCategory.AUTH;
            case 'S' -> ErrorCategory.STUDENT;
            case 'V' -> ErrorCategory.VALIDATION;
            case 'E' -> ErrorCategory.EXTERNAL;
            case 'F' -> ErrorCategory.FILE;
            default -> ErrorCategory.UNKNOWN;
        };
    }
    
    /**
     * 에러 심각도 판단 (Java 21 Pattern Matching 활용)
     */
    public ErrorSeverity getSeverity() {
        return switch (httpStatus.series()) {
            case CLIENT_ERROR -> switch (httpStatus) {
                case BAD_REQUEST, NOT_FOUND -> ErrorSeverity.LOW;
                case UNAUTHORIZED, FORBIDDEN -> ErrorSeverity.MEDIUM;
                default -> ErrorSeverity.MEDIUM;
            };
            case SERVER_ERROR -> ErrorSeverity.HIGH;
            default -> ErrorSeverity.LOW;
        };
    }
    
    /**
     * 에러 코드 카테고리
     */
    public enum ErrorCategory {
        COMMON, USER, AUTH, STUDENT, COURSE, VALIDATION, EXTERNAL, FILE, UNKNOWN
    }
    
    /**
     * 에러 심각도
     */
    public enum ErrorSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}