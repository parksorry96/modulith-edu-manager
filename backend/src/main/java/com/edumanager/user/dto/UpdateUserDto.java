package com.edumanager.user.dto;

import com.edumanager.shared.security.constants.SecurityConstants;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDto {

    @Size(min = 2, max = 50)
    private String name;

    @Pattern(regexp = SecurityConstants.Validation.PHONE_PATTERN, message = "전화번호는 010-0000-0000 형식이어야 합니다")
    private String phone;

    private String profileImageUrl;

    // 비밀번호 변경
    @Size(min = SecurityConstants.Validation.MIN_PASSWORD_LENGTH, max = SecurityConstants.Validation.MAX_PASSWORD_LENGTH)
    private String newPassword;

    @Size(min = SecurityConstants.Validation.MIN_PASSWORD_LENGTH, max = SecurityConstants.Validation.MAX_PASSWORD_LENGTH)
    private String currentPassword; // 비밀번호 변경시 현재 비밀번호 확인용
}
