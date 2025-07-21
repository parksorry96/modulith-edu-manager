package com.edumanager.shared.security.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * JWT 관련 설정을 관리하는 Properties 클래스
 * @ConfigurationProperties로 타입 안전성과 IDE 지원 확보
 */
@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    
    /**
     * RSA Private Key 파일 경로
     */
    @NotBlank
    private String privateKey = "classpath:keys/private.pem";
    
    /**
     * RSA Public Key 파일 경로  
     */
    @NotBlank
    private String publicKey = "classpath:keys/public.pem";
    
    /**
     * 액세스 토큰 만료시간 - Duration 타입은 @Min 검증 불가
     * 대신 setter에서 검증 로직 구현
     */
    @NotNull
    private Duration expiration = Duration.ofHours(24);
    
    /**
     * 리프레시 토큰 만료시간 - Duration 타입은 @Min 검증 불가
     * 대신 setter에서 검증 로직 구현
     */
    @NotNull
    private Duration refreshExpiration = Duration.ofDays(7);
    
    /**
     * JWT 발행자(Issuer)
     */
    @NotBlank
    private String issuer = "edu-manager";
    
    /**
     * JWT 대상(Audience)
     */
    @NotBlank
    private String audience = "edu-manager-client";
    
    /**
     * 액세스 토큰 만료시간 설정 - 최소 1초 이상 검증
     */
    public void setExpiration(Duration expiration) {
        if (expiration != null && expiration.toSeconds() < 1) {
            throw new IllegalArgumentException("액세스 토큰 만료시간은 최소 1초 이상이어야 합니다.");
        }
        this.expiration = expiration;
    }
    
    /**
     * 리프레시 토큰 만료시간 설정 - 최소 1초 이상 검증
     */
    public void setRefreshExpiration(Duration refreshExpiration) {
        if (refreshExpiration != null && refreshExpiration.toSeconds() < 1) {
            throw new IllegalArgumentException("리프레시 토큰 만료시간은 최소 1초 이상이어야 합니다.");
        }
        this.refreshExpiration = refreshExpiration;
    }
}
