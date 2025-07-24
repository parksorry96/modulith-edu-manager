package com.edumanager.user.dto;

import com.edumanager.shared.security.constants.SecurityConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "현재 비밀번호는 필수입니다")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Size(min = SecurityConstants.Validation.MIN_PASSWORD_LENGTH,
            max = SecurityConstants.Validation.MAX_PASSWORD_LENGTH,
            message = "비밀번호는 8자 이상 50자 이하여야 합니다")
    private String newPassword;
}
