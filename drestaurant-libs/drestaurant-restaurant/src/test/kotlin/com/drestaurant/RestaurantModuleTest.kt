package com.drestaurant

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchUnitRunner
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.junit.runner.RunWith


@RunWith(ArchUnitRunner::class)
@AnalyzeClasses(packages = ["com.drestaurant"])
internal class RestaurantModuleTest {

    /**
     * Testing the 'package' visibility/scope of 'internal' classes within the module.
     *
     * If you would have more packages/bounded contexts in this module, e.g: `com.rdestaurant.foo.domain` with classes accessing  `com.drestaurant.restaurant.domain` test will fail.
     * This will force your bounded contexts/packages to be more decoupled within the same module, and should be easy to extract them in separate modules in the future (if needed).
     *
     * In Java programming language you should only check the 'modifier'. Kotlin does not have 'package' scope
     */
    @ArchTest
    val classes_from_restaurant_domain_package_should_only_be_accessed_within_same_package = ArchRuleDefinition.classes().that().resideInAPackage("com.drestaurant.restaurant.domain").should().onlyBeAccessed().byClassesThat().resideInAPackage("com.drestaurant.restaurant.domain")

    // TODO  It would be nice to check if the all classes in this package ("com.drestaurant.restaurant.domain") are also `INTERNAL` for the module. Not sure how to do this with ArchUnit and Kotlin
}
