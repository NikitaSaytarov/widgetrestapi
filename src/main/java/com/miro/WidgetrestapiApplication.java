package com.miro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class WidgetrestapiApplication {
    
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        return loggingFilter;
    }

    //@Configuration
    //@EnableSwagger2
    //public class SwaggerConfig {
    //    @Bean
    //    public Docket api() {
    //        return new Docket(DocumentationType.SWAGGER_2)
    //                .select()
    //                .apis(RequestHandlerSelectors.any())
    //                .paths(PathSelectors.any())
    //                .build();
    //    }
    //}

    public static void main(String[] args) {
        SpringApplication.run(WidgetrestapiApplication.class, args);

    }

}
