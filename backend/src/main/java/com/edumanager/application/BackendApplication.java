package com.edumanager.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Education Manager Backend Application
 * Spring Modulith 아키텍처 기반 애플리케이션
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.edumanager.application",  // 애플리케이션 패키지
    "com.edumanager.shared",       // 공유 모듈 (Security, Exception 등)
    "com.edumanager.user",         // 사용자 모듈  
    "com.edumanager.student"       // 학생 모듈
})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
