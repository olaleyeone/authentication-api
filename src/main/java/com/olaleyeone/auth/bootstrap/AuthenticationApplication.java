package com.olaleyeone.auth.bootstrap;

import com.olaleyeone.auth.configuration.*;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.integration.etc.PhoneNumberServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Import({
        AppConfiguration.class,
        AuditTrailConfiguration.class,
        IntegrationConfiguration.class,
        WebConfiguration.class,
        SecurityConfiguration.class
})
@EnableAsync
public class AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }

    @Bean
    public PhoneNumberService phoneNumberService() {
        return new PhoneNumberServiceImpl("NG");
    }
}
