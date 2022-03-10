package com.liuuki.srb.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket adminApiConfig(){

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")  //分组的名称
                .apiInfo(adminApiInfo()) //分组的基本信息
                .select()
                //只显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();

    }

    @Bean
    public Docket webApiConfig(){

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(adminApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();

    }

    /**
     * 填写文档的基本信息
     * @return
     */
    private ApiInfo adminApiInfo(){

        return new ApiInfoBuilder()
                .title("项目后台管理API文档")
                .description("本文档描述了项目后台管理系统接口")
                .version("1.0")
                .contact(new Contact("刘禹麒","","liuuki@foxmail.com"))
                .build();
    }

    private ApiInfo webApiInfo(){

        return new ApiInfoBuilder()
                .title("项目网站API文档")
                .description("本文档描述了系统的网站接口")
                .version("1.0")
                .contact(new Contact("刘禹麒","","liuuki@foxmail.com"))
                .build();
    }
}
