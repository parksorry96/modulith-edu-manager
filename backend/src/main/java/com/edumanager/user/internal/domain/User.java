package com.edumanager.user.internal.domain;

import com.edumanager.shared.domain.BasicTimeEntity;
import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.shared.security.constants.SecurityConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email", unique = true),
                @Index(name = "idx_user_phone", columnList = "phone"),
                @Index(name = "idx_user_status", columnList = "status")
        }
)
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(name="user_seq", sequenceName = "user_sequence", allocationSize=1)
public class User extends BasicTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Comment("사용자 ID")
    private Long id;

    @NotBlank(message="이메일은 필수입니다.")
    @Email(message="올바른 이멩리 형식이 아닙니다")
    @Size(max= SecurityConstants.Validation.MAX_EMAIL_LENGTH)
    @Column(nullable=false, unique = true,length=SecurityConstants.Validation.MAX_EMAIL_LENGTH)
    @Comment("사용자 이메일(로그인 ID)")
    private String email;

    @NotBlank(message="비밀번호는 필수입니다.")
    @Size(min=SecurityConstants.Validation.MIN_PASSWORD_LENGTH,
    max=SecurityConstants.Validation.MAX_PASSWORD_LENGTH)
    private String password;

    @NotBlank(message="이름은 필수입니다.")
    @Size(min=2, max=50)
    @Column(nullable=false, length=50)
    @Comment("사용자 이름")
    private String name;

    @Pattern(regexp=SecurityConstants.Validation.PASSWORD_PATTERN,
    message="전화번호는 010-0000-0000 형식이어야 합니다.")
    @Column(length=13)
    @Comment("전화번호")
    private String phone;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Comment("사용자 권한")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=20)
    @Comment("계정 상태")
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @Comment("이메일 인증 여부")
    @Builder.Default
    private boolean emailVerified = false;

    @Comment("마지막 로그인 시간")
    private LocalDateTime lastLoginAt;

    @Comment("계정 잠금 시간")
    private LocalDateTime lockedAt;

    @Comment("로그인 실패 횟수")
    @Builder.Default
    private int loginFailCount = 0;

    @Comment("프로필 이미지 URL")
    private String profileImageUrl;

    public void addRole(UserRole role) {
        this.roles.add(role);
    }

    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }

    public boolean hasRole(UserRole role) {
        return this.roles.contains(role);
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.loginFailCount = 0;
    }

    public void increaseLoginFailCount() {
        this.loginFailCount++;
        if (this.loginFailCount >= 5) {
            this.lockAccount();
        }
    }

    public void lockAccount() {
        this.status = UserStatus.LOCKED;
        this.lockedAt = LocalDateTime.now();
    }

    public void unlockAccount() {
        this.status = UserStatus.ACTIVE;
        this.lockedAt = null;
        this.loginFailCount = 0;
    }

    public void activate() {
        if (this.status == UserStatus.PENDING) {
            this.status = UserStatus.ACTIVE;
            this.emailVerified = true;
        }
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public boolean isAccountLocked() {
        return this.status == UserStatus.LOCKED;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean canLogin() {
        return isActive() && !isAccountLocked();
    }
}
