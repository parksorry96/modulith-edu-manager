package com.edumanager.shared.security.config;

import com.edumanager.shared.security.constants.SecurityConstants;
import com.edumanager.shared.security.filter.JwtAuthenticationFilter;
import com.edumanager.shared.security.handler.JwtAccessDeniedHandler;
import com.edumanager.shared.security.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)        //@PreAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(SecurityConstants.Http.PUBLIC_ENDPOINTS)
                        .permitAll()

                        .requestMatchers(SecurityConstants.Http.ADMIN_ENDPOINTS)
                        .hasRole("ADMIN")

                        .requestMatchers(SecurityConstants.Http.INSTRUCTOR_ENDPOINTS)
                        .hasAnyRole("ADMIN","INSTRUCTOR")

                        .requestMatchers(SecurityConstants.Http.PARENT_ENDPOINTS)
                        .hasAnyRole("ADMIN", "INSTRUCTOR", "PARENT")

                        .requestMatchers(SecurityConstants.Http.STUDENT_ENDPOINTS)
                        .hasAnyRole("ADMIN", "INSTRUCTOR", "PARENT", "STUDENT")

                        .anyRequest()
                        .authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt->jwt.decoder(jwtDecoder))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exceptions ->exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .headers(headers->headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .contentTypeOptions(withDefaults())
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)
                        )
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.setAllowedOriginPatterns(
//                Arrays.asList(SecurityConstants.Http.CORS_ALLOWED_ORIGINS)
//        );
//
//        configuration.setAllowedMethods(
//                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
//        );
//
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//
//        configuration.setExposedHeaders(Arrays.asList(
//                "Authorization",
//                "X-Total-Count",
//                "X-Page-Number",
//                "X-Page-Size",
//                "X-User-Role"    // 사용자 역할 정보 노출
//        ));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
}
