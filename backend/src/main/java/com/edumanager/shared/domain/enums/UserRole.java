package com.edumanager.shared.domain.enums;

/**
 * 시스템 사용자 역할 정의
 * 
 * <p>권한 레벨: ADMIN > INSTRUCTOR > PARENT > STUDENT
 */
public enum UserRole {
    ADMIN("관리자", "ROLE_ADMIN", 1),
    INSTRUCTOR("강사", "ROLE_INSTRUCTOR", 2),
    PARENT("학부모", "ROLE_PARENT", 3),
    STUDENT("학생", "ROLE_STUDENT", 4);
    
    private final String description;
    private final String authority;
    private final int level;
    
    UserRole(String description, String authority, int level) {
        this.description = description;
        this.authority = authority;
        this.level = level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getAuthority() {
        return authority;
    }
    
    public int getLevel() {
        return level;
    }
    
    public boolean hasHigherAuthority(UserRole other) {
        return this.level < other.level;
    }
}