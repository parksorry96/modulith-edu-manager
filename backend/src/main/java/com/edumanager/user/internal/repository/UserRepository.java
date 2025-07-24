package com.edumanager.user.internal.repository;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.user.internal.domain.User;
import com.edumanager.user.internal.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    // 역할별 조회
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    Page<User> findByRole(@Param("role") UserRole role, Pageable pageable);

    // 검색
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime, u.loginFailCount = 0 WHERE u.id = :userId")
    void updateLastLoginSuccess(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    @Modifying
    @Query("UPDATE User u SET u.loginFailCount = u.loginFailCount + 1 WHERE u.id = :userId")
    void incrementLoginFailCount(@Param("userId") Long userId);
}
