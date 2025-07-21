package com.edumanager.shared.security.constants;

public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final class Jwt {
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String HEADER_NAME = "Authorization";
        public static final String TOKEN_TYPE_ACCESS = "ACCESS";
        public static final String TOKEN_TYPE_REFRESH = "REFRESH";
        public static final String AUTHORITIES_KEY = "roles";
        public static final String TOKEN_TYPE_KEY = "type";

        private Jwt() {}
    }

    public static final class Http {
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String[] PUBLIC_ENDPOINTS = {
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/refresh",
                "/api/health",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/health"
        };

        public static final String[] CORS_ALLOWED_ORIGINS = {
                "http://localhost:3000",
                "http://localhost:5173"
        };
        public static final String[] ADMIN_ENDPOINTS = {
                "/api/admin/**",
                "/api/users/admin/**",
                "/api/system/**"
        };

        public static final String[] INSTRUCTOR_ENDPOINTS = {
                "/api/courses/**",
                "/api/students/manage/**",  // 학생 관리
                "/api/enrollment/**",
                "/api/attendance/**",       // 출석 관리
                "/api/grades/**"           // 성적 관리
        };

        public static final String[] PARENT_ENDPOINTS = {
                "/api/parents/**",          // 부모 정보 관리
                "/api/children/**",         // 자녀 정보 조회
                "/api/children/*/grades",   // 자녀 성적 조회
                "/api/children/*/attendance", // 자녀 출석 조회
                "/api/children/*/courses",  // 자녀 수강 과목 조회
                "/api/payments/**",         // 수강료 납부 관련
                "/api/notifications/parent/**" // 부모용 알림
        };

        // 학생 전용 엔드포인트
        public static final String[] STUDENT_ENDPOINTS = {
                "/api/students/profile/**", // 학생 프로필 관리
                "/api/students/courses/**", // 본인 수강 과목
                "/api/students/grades/**",  // 본인 성적 조회
                "/api/students/attendance/**", // 본인 출석 조회
                "/api/homework/**",         // 숙제 제출
                "/api/notifications/student/**" // 학생용 알림
        };


        private Http() {}
    }

    public static final class Roles {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String INSTRUCTOR = "ROLE_INSTRUCTOR";
        public static final String STUDENT = "ROLE_STUDENT";
        public static final String PARENT = "ROLE_PARENT";



        private Roles() {}
    }

    public static final class Validation {
        public static final int MIN_PASSWORD_LENGTH = 8;
        public static final int MAX_PASSWORD_LENGTH = 50;
        public static final int MAX_USERNAME_LENGTH = 30;
        public static final int MAX_EMAIL_LENGTH = 100;

        public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        public static final String PHONE_PATTERN = "^010-\\d{4}-\\d{4}$";
        public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]";

        private Validation() {}
    }

}
