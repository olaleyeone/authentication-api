package com.olaleyeone.auth.bootstrap;

import com.olaleyeone.auth.configuration.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Import({
        AuditTrailConfiguration.class,
        IntegrationConfiguration.class,
        WebConfiguration.class,
        OpenApiConfiguration.class,
        SecurityConfiguration.class
})
@EnableJpaRepositories({
        "com.olaleyeone.auth.repository",
        "com.olaleyeone.audittrail.repository"
})
@EntityScan({
        "com.olaleyeone.data",
        "com.olaleyeone.auth.data",
        "com.olaleyeone.audittrail.entity"
})
@ComponentScan("com.olaleyeone.auth.service")
@EnableAsync
public class AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}
