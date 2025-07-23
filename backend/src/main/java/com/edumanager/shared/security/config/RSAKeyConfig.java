package com.edumanager.shared.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Slf4j
public class RSAKeyConfig {

    private final ResourceLoader resourceLoader;
    private final JwtProperties jwtProperties;

    public RSAKeyConfig(@Qualifier("webApplicationContext") ResourceLoader resourceLoader, 
                       JwtProperties jwtProperties) {
        this.resourceLoader = resourceLoader;
        this.jwtProperties = jwtProperties;
    }

    /**
     * RSA Private Key 로드
     * @return RSA 개인키 -> JWT 서명용
     */
    @Bean
    public RSAPrivateKey rsaPrivateKey(){
        try{
            Resource resource = resourceLoader.getResource(jwtProperties.getPrivateKey());

            String privateKeyContent = resource.getContentAsString(StandardCharsets.UTF_8);

            privateKeyContent = privateKeyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        }catch (Exception e){
            log.error("RSA Private Key 로드 실패",e);
            throw new RuntimeException("RSA Private Key 로드 실패",e);
        }
    }

    /**
     * RSA Public Key 로드
     * @return RSA 공개키(JWT 검증용)
     */
    @Bean
    public RSAPublicKey rsaPublicKey(){
        try{
            Resource resource = resourceLoader.getResource(jwtProperties.getPublicKey());

            String publicKeyContent = resource.getContentAsString(StandardCharsets.UTF_8);

            publicKeyContent = publicKeyContent
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decodedKey = Base64.getDecoder().decode(publicKeyContent);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPublicKey) keyFactory.generatePublic(keySpec);

        }catch (Exception e){
            log.error("RSA Public Key 로드 실패", e);
            throw new RuntimeException("RSA Public Key 로드 실패", e);
        }
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAPrivateKey privateKey, RSAPublicKey publicKey){
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("rsa-key-1")
                .build();

        //JWK를 JWKSet에 담기
        JWKSet jwkSet = new JWKSet(rsaKey);
        //키를 변경하지 못하게 불변 JWK Source 반환
        return new ImmutableJWKSet<>(jwkSet);
    }
    //JWT 토큰 생성용 인코더
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource){
        return new NimbusJwtEncoder(jwkSource);
    }

    //JWT 토큰 디코더
    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey){
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

}
