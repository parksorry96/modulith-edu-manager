package com.edumanager;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit5.AnalyzeClasses;
import com.tngtech.archunit.junit5.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * ArchUnit을 사용한 아키텍처 규칙 검증
 */
@AnalyzeClasses(packages = "com.edumanager")
class ArchUnitTest {
    
    @ArchTest
    static final ArchRule moduleShouldNotDependOnInternalPackagesOfOtherModules = 
        noClasses()
            .that().resideInAPackage("com.edumanager.user..")
            .should().dependOnClassesThat()
            .resideInAPackage("com.edumanager.student.internal..");
    
    @ArchTest
    static final ArchRule internalPackagesShouldNotBeAccessedFromOutside = 
        noClasses()
            .that().resideOutsideOfPackage("com.edumanager.user..")
            .should().dependOnClassesThat()
            .resideInAPackage("com.edumanager.user.internal..");
    
    @ArchTest
    static final ArchRule controllersShouldBeInWebPackage = 
        classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..internal.web..");
    
    @ArchTest
    static final ArchRule servicesShouldBeInServicePackage = 
        classes()
            .that().haveSimpleNameEndingWith("Service")
            .or().haveSimpleNameEndingWith("ServiceImpl")
            .should().resideInAPackage("..service..");
    
    @ArchTest
    static final ArchRule repositoriesShouldBeInRepositoryPackage = 
        classes()
            .that().haveSimpleNameEndingWith("Repository")
            .should().resideInAPackage("..repository..");
    
    @Test
    void layeredArchitectureShouldBeRespected() {
        JavaClasses importedClasses = new ClassFileImporter()
            .importPackages("com.edumanager");
        
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("Controllers").definedBy("..internal.web..")
            .layer("Services").definedBy("..service..")
            .layer("Repositories").definedBy("..repository..")
            .layer("Domain").definedBy("..domain..")
            .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
            .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers")
            .whereLayer("Repositories").mayOnlyBeAccessedByLayers("Services")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Services", "Repositories")
            .check(importedClasses);
    }
}