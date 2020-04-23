package com.olaleyeone.auth.bootstrap;

import com.olaleyeone.auth.configuration.AuditTrailConfiguration;
import com.olaleyeone.auth.configuration.IntegrationConfiguration;
import com.olaleyeone.auth.configuration.OpenApiConfiguration;
import com.olaleyeone.auth.configuration.WebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Import({
        AuditTrailConfiguration.class,
        WebConfiguration.class,
        OpenApiConfiguration.class,
        IntegrationConfiguration.class
})
@EnableJpaRepositories({"com.olaleyeone.auth.repository"})
@EntityScan({"com.olaleyeone.data", "com.olaleyeone.auth.data"})
@ComponentScan("com.olaleyeone.auth.service")
public class AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}
