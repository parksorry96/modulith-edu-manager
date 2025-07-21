/**
 * User 모듈 - 사용자 관리 및 인증 기능을 담당합니다.
 * 
 * <p>이 모듈은 시스템의 모든 사용자(학생, 강사, 학부모, 관리자)에 대한 
 * 계정 관리 및 인증 기능을 제공합니다:
 * <ul>
 *   <li>사용자 등록 및 프로필 관리</li>
 *   <li>로그인/로그아웃 처리</li>
 *   <li>비밀번호 재설정</li>
 *   <li>역할(Role) 기반 권한 관리</li>
 * </ul>
 * 
 * <p>발행 이벤트:
 * <ul>
 *   <li>UserRegisteredEvent - 새 사용자 등록 시</li>
 *   <li>UserProfileUpdatedEvent - 프로필 수정 시</li>
 *   <li>UserDeactivatedEvent - 계정 비활성화 시</li>
 * </ul>
 * 
 * @since 1.0.0
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"shared"},
    displayName = "User Module"
)
package com.edumanager.user;