package com.dzhy.manage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @ClassName Swagger2Config
 * @Description swagger2 配置
 * @Author alex
 * @Date 2018/10/31
 **/
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzhy.manage.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("alex", "https://github.com/JokerAlex/Manage", "alex.zhao1023@gmail.com");
        return new ApiInfoBuilder()
                .title("DZHY-库房管理在线api文档")
                .description("Swagger2 构建在线api文档")
                .termsOfServiceUrl("https://github.com/JokerAlex/Manage")
                .contact(contact)
                .version("1.0")
                .build();
    }
}
