/**
 * Student 모듈 - 학생 관리 기능을 담당합니다.
 * 
 * <p>이 모듈은 학생의 학원 생활과 관련된 모든 기능을 제공합니다:
 * <ul>
 *   <li>학생 정보 관리 (프로필, 연락처)</li>
 *   <li>학부모 연결 관리</li>
 *   <li>출결 관리</li>
 *   <li>성적 및 평가 조회</li>
 *   <li>학습 진도 추적</li>
 * </ul>
 * 
 * <p>발행 이벤트:
 * <ul>
 *   <li>StudentEnrolledEvent - 학생 등록 시</li>
 *   <li>AttendanceRecordedEvent - 출석 체크 시</li>
 *   <li>GradeUpdatedEvent - 성적 업데이트 시</li>
 * </ul>
 * 
 * <p>구독 이벤트:
 * <ul>
 *   <li>UserRegisteredEvent - 학생 계정 생성 시 프로필 초기화</li>
 *   <li>CourseCompletedEvent - 수강 완료 시 학습 이력 업데이트</li>
 * </ul>
 * 
 * @since 1.0.0
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"shared", "user"},
    displayName = "Student Module"
)
package com.edumanager.student;