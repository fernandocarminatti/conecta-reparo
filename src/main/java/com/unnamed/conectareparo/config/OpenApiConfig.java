package com.unnamed.conectareparo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI conectaReparoOpenApiConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("Conecta Reparo API")
                        .version("v1")
                        .description("API para a plataforma Conecta Reparo, que conecta necessidades de manutenção de centros de saúde com voluntários da comunidade.")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}