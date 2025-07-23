/**
 * PreRegistration 모듈 - 사전등록 및 인증코드 기반 회원가입을 담당합니다.
 * 
 * <p>이 모듈은 학원의 기존 학생 정보를 기반으로 한 안전한 회원가입 프로세스를 제공합니다:
 * <ul>
 *   <li>학생/학부모 정보 사전등록</li>
 *   <li>인증코드 생성 및 발송</li>
 *   <li>인증코드 검증</li>
 *   <li>사전등록 정보 기반 회원가입</li>
 * </ul>
 * 
 * <p>발행 이벤트:
 * <ul>
 *   <li>PreRegistrationCreatedEvent - 사전등록 생성 시</li>
 *   <li>VerificationCodeSentEvent - 인증코드 발송 시</li>
 *   <li>PreRegistrationVerifiedEvent - 인증 완료 시</li>
 * </ul>
 * d
 * <p>구독 이벤트:
 * <ul>
 *   <li>UserRegisteredEvent - 회원가입 완료 시 상태 업데이트</li>
 * </ul>
 * 
 * @since 1.0.0
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"shared", "user"},
    displayName = "PreRegistration Module"
)
package com.edumanager.preregistration;