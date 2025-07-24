package com.edumanager.user.dto;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.shared.security.constants.SecurityConstants;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank @Email
    @Size(max = SecurityConstants.Validation.MAX_EMAIL_LENGTH)
    private String email;

    @NotBlank
    @Size(min = SecurityConstants.Validation.MIN_PASSWORD_LENGTH,
            max = SecurityConstants.Validation.MAX_PASSWORD_LENGTH)
    @Pattern(regexp = SecurityConstants.Validation.PASSWORD_PATTERN + "{8,50}",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다")
    private String password;

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @Pattern(regexp = SecurityConstants.Validation.PHONE_PATTERN)
    private String phone;

    @NotNull
    private UserRole role;
}
