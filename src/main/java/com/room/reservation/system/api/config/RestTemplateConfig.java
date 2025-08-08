package com.room.reservation.system.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    // 변경사항 테스트

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 