package com.olaleyeone.auth.bootstrap;

import com.olaleyeone.auth.configuration.OpenApiConfiguration;
import com.olaleyeone.auth.configuration.WebConfiguration;
import com.olaleyeone.auth.service.PhoneNumberService;
import com.olaleyeone.auth.service.PhoneNumberServiceImpl;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.Key;

@SpringBootApplication
@Import({WebConfiguration.class, OpenApiConfiguration.class})
@EnableJpaRepositories({"com.olaleyeone.auth.repository"})
@EntityScan("com.olaleyeone.auth.data")
@ComponentScan("com.olaleyeone.auth.service")
public class AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }

    @Bean
    public Key jwtEncryptionKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    @Bean
    public PhoneNumberService phoneNumberService() {
        return new PhoneNumberServiceImpl("US");
    }
}
