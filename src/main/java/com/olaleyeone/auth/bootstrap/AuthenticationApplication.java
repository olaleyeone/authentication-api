package com.olaleyeone.auth.bootstrap;

import com.olaleyeone.auth.configuration.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Import({
        DataConfiguration.class,
        ServiceConfiguration.class,
        AuditTrailConfiguration.class,
        IntegrationConfiguration.class,
        WebConfiguration.class,
        OpenApiConfiguration.class,
        SecurityConfiguration.class
})
@EnableAsync
public class AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}
