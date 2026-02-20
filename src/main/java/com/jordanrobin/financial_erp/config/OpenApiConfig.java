package com.jordanrobin.financial_erp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Financial ERP API")
                        .version("1.0.0")
                        .description("API REST pour l'ERP financier SaaS pour PME")
                        .contact(new Contact()
                                .name("Jordan Robin")
                                .email("contact@example.com"))
                        .license(new License()
                                .name("Proprietary")));
    }
}
