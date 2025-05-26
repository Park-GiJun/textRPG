package com.gijun.textrpg.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ArchitectureTest {

    private lateinit var importedClasses: JavaClasses

    @BeforeEach
    fun setup() {
        importedClasses = ClassFileImporter()
            .importPackages("com.gijun.textrpg")
    }

    @Test
    fun `should follow hexagonal architecture`() {
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Adapter").definedBy("..adapter..")
            .layer("Configuration").definedBy("..configuration..")
            .whereLayer("Domain").mayNotAccessAnyLayer()
            .whereLayer("Application").mayOnlyAccessLayers("Domain")
            .whereLayer("Adapter").mayOnlyAccessLayers("Application", "Domain")
            .whereLayer("Configuration").mayOnlyAccessLayers("Adapter", "Application", "Domain")
            .check(importedClasses)
    }

    @Test
    fun `domain should not depend on frameworks`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "javax..",
                "jakarta.."
            )
            .check(importedClasses)
    }

    @Test
    fun `ports should be interfaces`() {
        classes()
            .that().resideInAPackage("..port..")
            .should().beInterfaces()
            .check(importedClasses)
    }

    @Test
    fun `use cases should have UseCase suffix`() {
        classes()
            .that().resideInAPackage("..port.in..")
            .and().areInterfaces()
            .should().haveSimpleNameEndingWith("UseCase")
            .check(importedClasses)
    }

    @Test
    fun `repositories should have Repository suffix`() {
        classes()
            .that().resideInAPackage("..port.out..")
            .and().areInterfaces()
            .and().haveSimpleNameContaining("Repository")
            .should().haveSimpleNameEndingWith("Repository")
            .check(importedClasses)
    }

    @Test
    fun `services should be in application layer`() {
        classes()
            .that().haveSimpleNameEndingWith("Service")
            .and().areNotInterfaces()
            .should().resideInAPackage("..application.service..")
            .check(importedClasses)
    }

    @Test
    fun `controllers should be in web adapter`() {
        classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..adapter.in.web..")
            .check(importedClasses)
    }
}
