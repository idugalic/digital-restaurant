package com.drestaurant.configuration

import com.google.common.base.Predicates
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfiguration {

    fun apiInfo() = ApiInfoBuilder()
            .title("Digital Restaurant")
            .description("REST API")
            .termsOfServiceUrl("")
            .version("1.0.0")
            .contact(Contact("Ivan Dugalic", "http://idugalic.pro", "idugalic@gmail.com"))
            .build()


    @Bean
    fun api() = Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(Predicates.not(PathSelectors.regex("/error.*")))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())

}