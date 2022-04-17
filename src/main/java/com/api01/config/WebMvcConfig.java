package com.api01.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //스프링 빈으로 등록
public class WebMvcConfig implements WebMvcConfigurer {
    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //모든 경로에 대해
        registry.addMapping("/**")
                //Origin이 http://localhost:3000에 대해
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
                .allowedHeaders("*") //모든 헤더
                .allowCredentials(true) //인증
                .maxAge(MAX_AGE_SECS);  // 3600초 동안 preflight 결과를 캐시에 저장
       /* WebMvcConfigurer.super.addCorsMappings(registry);*/
    }
}
