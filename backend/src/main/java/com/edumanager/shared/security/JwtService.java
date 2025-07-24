package com.edumanager.shared.security;

import com.edumanager.shared.exception.BusinessException;
import com.edumanager.shared.exception.ErrorCode;
import com.edumanager.shared.security.config.JwtProperties;
import com.edumanager.shared.security.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;

    public String generateAccessToken(Authentication authentication){
        //Properties에서 만료시간을 가져와서 초단위로 변환
        long expirationSeconds = jwtProperties.getExpiration().toSeconds();

        return generateToken(authentication,expirationSeconds, SecurityConstants.Jwt.TOKEN_TYPE_ACCESS);
    }

    public String generateRefreshToken(Authentication authentication){
        long expirationSeconds = jwtProperties.getRefreshExpiration().toSeconds();

        return generateToken(authentication,expirationSeconds,SecurityConstants.Jwt.TOKEN_TYPE_REFRESH);
    }

    private String generateToken(Authentication authentication, long expirationSeconds, String tokenType){
        // Instant -> 불변객체
        Instant now =  Instant.now();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        JwtClaimsSet claims=JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())           // 발행자 (Properties에서)
                .subject(authentication.getName())           // 주체 (사용자 ID/이름)
                .audience(List.of(jwtProperties.getAudience())) // 대상 (Properties에서)
                .issuedAt(now)                              // 발행 시간
                .expiresAt(now.plusSeconds(expirationSeconds)) // 만료 시간
                .claim(SecurityConstants.Jwt.AUTHORITIES_KEY, authorities)  // 권한 정보
                .claim(SecurityConstants.Jwt.TOKEN_TYPE_KEY, tokenType)     // 토큰 타입
                .build();

        // claims 를 파라미터로 변환허여 토큰 생성 및 반환
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


    /**
     * JWT 토큰에서 사용자명(Subject) 추출
     * @param token JWT 토큰 문자열
     * @return 사용자명
     */
    public String extractUsername(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            log.error("JWT 토큰에서 사용자명 추출 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_TOKEN, e);
        }
    }

    /**
     * JWT 토큰 유효성 검증
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            // 토큰 디코딩 시도 - 서명 검증, 만료시간 체크 등 자동 수행
            Jwt jwt = jwtDecoder.decode(token);

            // 현재 시간과 만료시간 비교 (추가 안전장치)
            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(Instant.now())) {
                log.warn("JWT 토큰이 만료됨: {}", jwt.getExpiresAt());
                throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
            }

            // 모든 검증 통과
            return true;

        } catch (JwtException e) {
            // JWT 디코딩 실패 (서명 오류, 형식 오류 등)
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰에서 권한 정보 추출
     * @param token JWT 토큰
     * @return 권한 목록 (ROLE_ADMIN, ROLE_USER 등)
     */
    public List<String> extractAuthorities(String token) {
        try {
            // JWT 디코딩
            Jwt jwt = jwtDecoder.decode(token);

            // 권한 클레임 가져오기 (Constants에서 키 사용)
            String roles = jwt.getClaimAsString(SecurityConstants.Jwt.AUTHORITIES_KEY);

            // 쉼표로 구분된 문자열을 List로 변환
            return roles != null ?
                    List.of(roles.split(",")) : // "ROLE_ADMIN,ROLE_USER" → ["ROLE_ADMIN", "ROLE_USER"]
                    List.of();                   // 권한이 없으면 빈 리스트

        } catch (JwtException e) {
            log.error("JWT 토큰에서 권한 추출 실패: {}", e.getMessage());
            return List.of(); // 실패 시 빈 권한 리스트 반환
        }
    }

    /**
     * JWT 토큰 타입 확인 (ACCESS/REFRESH)
     * @param token JWT 토큰
     * @return 토큰 타입
     */
    public String extractTokenType(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);

            // 토큰 타입 클레임 가져오기
            return jwt.getClaimAsString(SecurityConstants.Jwt.TOKEN_TYPE_KEY);

        } catch (JwtException e) {
            log.error("JWT 토큰 타입 추출 실패: {}", e.getMessage());
            return null;
        }
    }
    public Long getAccessTokenExpiration() {
        return jwtProperties.getExpiration().toSeconds();
    }

    public Long getRefreshTokenExpiration() {
        return jwtProperties.getRefreshExpiration().toSeconds();
    }

    public String refreshAccessToken(String refreshToken) {
        try{
            if(!validateToken(refreshToken)){
                throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
            }
            String tokenType=extractTokenType(refreshToken);
            if(!tokenType.equals(SecurityConstants.Jwt.TOKEN_TYPE_REFRESH)){
                throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
            }

            Jwt jwt=jwtDecoder.decode(refreshToken);
            String subject=jwt.getSubject();
            String authorities=jwt.getClaimAsString(SecurityConstants.Jwt.AUTHORITIES_KEY);

            Instant now = Instant.now();
            long expirationSeconds = jwtProperties.getExpiration().toSeconds();

            JwtClaimsSet claims= JwtClaimsSet.builder()
                    .issuer(jwtProperties.getIssuer())
                    .id(UUID.randomUUID().toString())
                    .subject(subject)
                    .audience(List.of(jwtProperties.getAudience()))
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expirationSeconds))
                    .claim(SecurityConstants.Jwt.AUTHORITIES_KEY, authorities)
                    .claim(SecurityConstants.Jwt.TOKEN_TYPE_KEY, SecurityConstants.Jwt.TOKEN_TYPE_ACCESS)
                    .build();
            return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }catch(JwtException e){
            log.error("리프레시 토큰 실패:{}",e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
