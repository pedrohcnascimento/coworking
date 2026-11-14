package com.phcnforge.coworking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI coworkingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestão de Coworking & Desk Booking")
                        .description(
                                "Sistema de agendamento de estações de trabalho, salas de reunião e controle de membros baseado em DDD.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("pedrohenrique.cn07@gmail.com")));
    }
}
