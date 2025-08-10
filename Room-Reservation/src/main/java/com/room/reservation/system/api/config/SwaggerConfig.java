package com.room.reservation.system.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("회의실 예약 시스템 API")
                        .version("1.0")
                        .description("회의실 예약 및 결제 시스템 API 문서입니다."));
    }
}
