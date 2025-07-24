package com.edumanager.user.dto;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.shared.security.constants.SecurityConstants;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserDto {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = SecurityConstants.Validation.MAX_EMAIL_LENGTH)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = SecurityConstants.Validation.MIN_PASSWORD_LENGTH,
            max = SecurityConstants.Validation.MAX_PASSWORD_LENGTH,
            message = "비밀번호는 8자 이상 50자 이하여야 합니다")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50)
    private String name;

    @Pattern(regexp = SecurityConstants.Validation.PHONE_PATTERN,
            message = "전화번호는 010-0000-0000 형식이어야 합니다")
    private String phone;

    @NotNull(message = "권한은 필수입니다")
    private UserRole role;

    // 관리자가 직접 생성하는 경우 추가 옵션
    @Builder.Default
    private boolean emailVerified = false;
    @Builder.Default
    private boolean active = true;
}
