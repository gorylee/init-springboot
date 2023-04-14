package com.openAi.yunchat.core.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
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

@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class Swagger2Config {

    /**
     * http://localhost:4001/swagger-ui.html
     * http://localhost:4001/doc.html
     * swagger2核心配置 docket
     * @return
     */
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())//定义api文档汇总信息
                .enable(true)
                .select().apis(RequestHandlerSelectors.basePackage("com.xl.finance.module"))//指定扫描controller位置
                .paths(PathSelectors.any())//所有controller
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("api")//文档标题
                .contact(new Contact("砯崖","https://www.bugyy.com","double_hill@163.com"))
                .description("我是一条酸菜鱼,又酸又菜又多鱼~~~")//详细信息
                .version("1.0.1")//版本号
                .termsOfServiceUrl("https://www.bugyy.com")//网站地址
                .build();


    }
}
