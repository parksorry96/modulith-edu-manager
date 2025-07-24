package com.edumanager.user.dto;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.user.internal.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {
    private String keyword;
    private UserRole role;
    private UserStatus status;
}
