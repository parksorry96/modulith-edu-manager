package com.edumanager.core.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix="app.cors")
public class CorsProperties {

    @NotEmpty
    private List<String> allowedOrigins= List.of(
            "http://localhost:3000",
            "http://localhost:5173"
    );

    @NotEmpty
    private List<String> allowedMethods= List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
    );

    @NotEmpty
    private List<String> allowedHeaders= List.of("*");

    @NotEmpty
    private List<String> exposedHeaders= List.of(
            "Authorization",
            "X-Total-Count",
            "X-Page-Number",
            "X-Page-Size",
            "X-User-Role"
    );

    @NotNull
    private Boolean allowCredentials = true;

    private Long maxAge = 3600L; // 1시간


}
