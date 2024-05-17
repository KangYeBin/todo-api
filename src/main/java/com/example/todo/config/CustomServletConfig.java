package com.example.todo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomServletConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // CORS 설정을 적용할 url
                .allowedOrigins("*")    // 자원 공유를 허라할 origin 설정 (origin : 프로토콜, ip 주소, 포트 번호)
                .allowedMethods("HEAD", "GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS")  // 요청 방식
                .maxAge(30) // 원하는 시간만큼 기존에 허락했던 요청 정보를 기억할 시간
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type");  // 요청을 허락할 헤더 정보 종류
    }
}
