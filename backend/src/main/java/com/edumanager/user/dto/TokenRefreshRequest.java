package com.edumanager.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TokenRefreshRequest {
    @NotBlank(message = "Refresh token은 필수입니다")
    private String refreshToken;
}
