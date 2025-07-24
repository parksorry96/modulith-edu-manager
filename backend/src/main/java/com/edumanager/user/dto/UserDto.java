package com.edumanager.user.dto;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.user.internal.domain.UserStatus;
import lombok.*;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private Set<UserRole> roles;
    private UserStatus status;
    private boolean emailVerified;
    private LocalDateTime createAt;
    private LocalDateTime lastLoginAt;

}
