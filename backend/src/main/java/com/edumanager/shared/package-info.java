/**
 * Shared 모듈 - 시스템 전반에서 사용되는 공통 기능을 제공합니다.
 * 
 * <p>이 모듈은 다른 모든 도메인 모듈에서 사용할 수 있는 공통 컴포넌트를 포함합니다:
 * <ul>
 *   <li>보안 (Security) - JWT 인증/인가</li>
 *   <li>예외 처리 (Exception) - 공통 비즈니스 예외</li>
 *   <li>응답 DTO (Response) - 표준화된 API 응답</li>
 *   <li>유틸리티 (Utility) - 공통 헬퍼 클래스</li>
 *   <li>이벤트 (Events) - 도메인 간 통신용 이벤트</li>
 * </ul>
 * 
 * @since 1.0.0
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {},
    displayName = "Shared Module"
)
package com.edumanager.shared;