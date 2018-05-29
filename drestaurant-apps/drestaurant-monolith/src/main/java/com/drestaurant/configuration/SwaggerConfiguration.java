package com.drestaurant.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
//@Import({SpringDataRestConfiguration.class})
public class SwaggerConfiguration {   
	
	ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Digital Restaurant")
            .description("REST API")
            .termsOfServiceUrl("")
            .version("1.0.0")
            .contact(new Contact("Ivan Dugalic", "http://idugalic.pro", "idugalic@gmail.com"))
            .build();
    }
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())  
          .paths(Predicates.not(PathSelectors.regex("/error.*")))
          .paths(PathSelectors.any())                          
          .build()
          .apiInfo(apiInfo());                                           
    }
}