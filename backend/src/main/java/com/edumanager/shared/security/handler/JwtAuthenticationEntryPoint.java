package com.edumanager.shared.security.handler;

import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.shared.dto.response.ErrorResponse;
import com.edumanager.shared.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    // JSON 응답 생성을 위한 ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 인증 실패 시 호출되는 메서드
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authException 인증 예외 (Spring Security에서 발생)
     * @throws IOException JSON 응답 생성 실패 시
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        // 요청 정보 로깅 (보안 감사용)
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = getClientIpAddress(request);

        log.warn("JWT 인증 실패 - URI: {} {}, IP: {}, 사유: {}",
                method, requestURI, remoteAddr, authException.getMessage());

        // 구조화된 에러 응답 생성
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message("인증이 필요합니다. 로그인 후 다시 시도해주세요.")
                .timestamp(LocalDateTime.now())
                .path(requestURI)
                .category(ErrorCode.ErrorCategory.AUTH.name())
                .severity(ErrorCode.ErrorSeverity.MEDIUM.name())
                .build();

        // ApiResponse로 래핑
        ApiResponse<Void> apiResponse = ApiResponse.error(errorResponse);

        // HTTP 응답 헤더 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // CORS 헤더 추가 (프론트엔드에서 에러 응답 읽을 수 있도록)
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        // JSON 응답 작성
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);

        log.debug("401 Unauthorized 응답 전송 완료: {}", requestURI);
    }

    /**
     * 클라이언트 실제 IP 주소 추출
     * 프록시, 로드밸런서 환경 고려
     *
     * @param request HTTP 요청 객체
     * @return 클라이언트 IP 주소
     */
    private String getClientIpAddress(HttpServletRequest request) {
        // X-Forwarded-For 헤더 확인 (프록시 경유 시)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // 첫 번째 IP가 실제 클라이언트 IP
            return xForwardedFor.split(",")[0].trim();
        }

        // X-Real-IP 헤더 확인 (Nginx 등)
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        // Proxy-Client-IP 헤더 확인
        String proxyClientIP = request.getHeader("Proxy-Client-IP");
        if (proxyClientIP != null && !proxyClientIP.isEmpty()) {
            return proxyClientIP;
        }

        // WL-Proxy-Client-IP 헤더 확인 (WebLogic)
        String wlProxyClientIP = request.getHeader("WL-Proxy-Client-IP");
        if (wlProxyClientIP != null && !wlProxyClientIP.isEmpty()) {
            return wlProxyClientIP;
        }

        // 기본값: 직접 연결된 클라이언트 IP
        return request.getRemoteAddr();
    }
}
