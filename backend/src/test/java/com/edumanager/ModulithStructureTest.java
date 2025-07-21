package com.edumanager;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Spring Modulith 구조 검증 테스트
 * 
 * <p>모듈 간 의존성 규칙 검증 및 문서화
 */
class ModulithStructureTest {
    
    private final ApplicationModules modules = ApplicationModules.of(BackendApplication.class);
    
    @Test
    void verifyModularStructure() {
        // 모듈 구조 검증 - 의존성 규칙 위반 시 테스트 실패
        modules.verify();
    }
    
    @Test
    void printModuleStructure() {
        // 모듈 구조 출력 (디버깅용)
        modules.forEach(System.out::println);
    }
    
    @Test
    void generateModulithDocumentation() {
        // PlantUML 다이어그램 생성 (선택사항)
        new Documenter(modules)
            .writeDocumentation()
            .writeIndividualModulesAsPlantUml();
    }
}