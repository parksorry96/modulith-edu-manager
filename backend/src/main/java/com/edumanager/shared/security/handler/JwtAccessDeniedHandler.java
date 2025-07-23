package com.edumanager.shared.security.handler;

import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.shared.dto.response.ErrorResponse;
import com.edumanager.shared.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // Spring이 관리하는 ObjectMapper 주입 (JSR310 모듈 자동 포함)
    private final ObjectMapper objectMapper;

    /**
     * 권한 부족 시 호출되는 메서드
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param accessDeniedException 접근 거부 예외
     * @throws IOException JSON 응답 생성 실패 시
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        // 현재 인증된 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymous";
        String authorities = authentication != null ?
                authentication.getAuthorities().toString() : "[]";

        // 요청 정보 로깅 (보안 감사용)
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = getClientIpAddress(request);

        log.warn("JWT 권한 부족 - 사용자: {}, 권한: {}, URI: {} {}, IP: {}, 사유: {}",
                username, authorities, method, requestURI, remoteAddr,
                accessDeniedException.getMessage());

        // 권한별 맞춤 에러 메시지 생성
        String customMessage = generateCustomErrorMessage(requestURI, authorities);

        // 구조화된 에러 응답 생성
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.ACCESS_DENIED.getCode())
                .message(customMessage)
                .timestamp(LocalDateTime.now())
                .path(requestURI)
                .category(ErrorCode.ErrorCategory.AUTH.name())
                .severity(ErrorCode.ErrorSeverity.MEDIUM.name())
                .build();

        // ApiResponse로 래핑
        ApiResponse<Void> apiResponse = ApiResponse.error(errorResponse);

        // HTTP 응답 헤더 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");



        // JSON 응답 작성
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);

        log.debug("403 Forbidden 응답 전송 완료: {} for user {}", requestURI, username);
    }

    /**
     * 요청 경로와 사용자 권한에 따른 맞춤형 에러 메시지 생성
     *
     * @param requestURI 요청 URI
     * @param authorities 사용자 권한 목록
     * @return 맞춤형 에러 메시지
     */
    private String generateCustomErrorMessage(String requestURI, String authorities) {
        // Java 21의 Pattern Matching for Switch 활용
        return switch (requestURI) {
            case String uri when uri.startsWith("/api/admin") ->
                    "관리자 권한이 필요합니다. 관리자에게 문의하세요.";

            case String uri when uri.startsWith("/api/courses") ->
                    "강사 권한이 필요합니다. 강사 계정으로 로그인하세요.";

            case String uri when uri.startsWith("/api/parents") || uri.startsWith("/api/children") ->
                    "부모 권한이 필요합니다. 등록된 학부모 계정으로 로그인하세요.";

            case String uri when uri.startsWith("/api/students") ->
                    "학생 권한이 필요하거나 본인의 정보만 접근 가능합니다.";

            case String uri when uri.contains("/payments") ->
                    "결제 정보 접근 권한이 없습니다. 본인 또는 자녀의 결제 정보만 확인 가능합니다.";

            default ->
                    String.format("해당 리소스에 접근할 권한이 없습니다. 현재 권한: %s", authorities);
        };
    }

    /**
     * 클라이언트 실제 IP 주소 추출 (EntryPoint와 동일한 로직)
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        String proxyClientIP = request.getHeader("Proxy-Client-IP");
        if (proxyClientIP != null && !proxyClientIP.isEmpty()) {
            return proxyClientIP;
        }

        String wlProxyClientIP = request.getHeader("WL-Proxy-Client-IP");
        if (wlProxyClientIP != null && !wlProxyClientIP.isEmpty()) {
            return wlProxyClientIP;
        }

        return request.getRemoteAddr();
    }
}
