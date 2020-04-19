package com.olaleyeone.auth.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Authentication API").version("1.0")
                        .contact(contact())
                        .description(
                                "Authentication RESTful web service using springdoc-openapi and OpenAPI 3."));
    }

    private Contact contact() {
        Contact contact = new Contact();
        contact.setName("Olaleye Afolabi");
        contact.setEmail("olaleyeone@gmail.com");
        return contact;
    }
}
