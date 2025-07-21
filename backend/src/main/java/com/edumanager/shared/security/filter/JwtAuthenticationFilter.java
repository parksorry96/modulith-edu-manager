package com.edumanager.shared.security.filter;


import com.edumanager.shared.security.JwtService;
import com.edumanager.shared.security.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


// Authorization 헤더의 JWT 토큰 검증, security filter chain 에서 UsernamePasswordAuthenticationFilter 이전에 실행
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String token=extractTokenFromRequest(request);

            if(token!=null && SecurityContextHolder.getContext().getAuthentication()!=null){
                if(jwtService.validateToken(token)){
                    String username=jwtService.extractUsername(token);
                    List<String> authorities=jwtService.extractAuthorities(token);

                    List<SimpleGrantedAuthority> grantedAuthorities= authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, token, grantedAuthorities);
                    // IP 주소 세션 ID 등을 설정
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("JWT 인증성공 사용자 : {}, 권한 : {}",username,authorities);
                }else{
                    log.warn("유효하지 않은 JWT 토큰 : {}", token);
                }
            }
        }catch(Exception e){
            log.error("JWT 인증 처리 중 오류발생",e );

            //SecurityContext 초기화
            SecurityContextHolder.clearContext();
        }

        //다음 필터로 요청 전달
        filterChain.doFilter(request,response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     * Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열 (Bearer 접두어 제거된 상태)
     */
    private String extractTokenFromRequest(HttpServletRequest request) {

        // Authorization 헤더 값 가져오기
        String bearerToken = request.getHeader(SecurityConstants.Http.AUTHORIZATION_HEADER);

        // 헤더가 존재하고 "Bearer "로 시작하는지 확인
        if (StringUtils.hasText(bearerToken) &&
                bearerToken.startsWith(SecurityConstants.Jwt.TOKEN_PREFIX)) {

            // "Bearer " 접두어 제거하여 순수 JWT 토큰만 반환
            return bearerToken.substring(SecurityConstants.Jwt.TOKEN_PREFIX.length());
        }

        return null; // 토큰이 없거나 형식이 잘못된 경우
    }

    /**
     * 특정 요청에 대해 필터를 건너뛸지 결정
     * 예: Swagger UI, 헬스체크 등은 JWT 검증 불필요
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 공개 엔드포인트는 JWT 검증 건너뛰기
        for (String publicEndpoint : SecurityConstants.Http.PUBLIC_ENDPOINTS) {
            if (path.matches(publicEndpoint.replace("**", ".*"))) {
                log.debug("공개 엔드포인트로 JWT 필터 건너뛰기: {}", path);
                return true;
            }
        }
        return false; // 일반적인 경우는 필터 적용
    }
}
