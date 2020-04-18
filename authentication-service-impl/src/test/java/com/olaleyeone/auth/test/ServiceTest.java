package com.olaleyeone.auth.test;

import com.olaleyeone.auth.service.PasswordService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(ServiceTest.$Config.class)
public class ServiceTest extends EntityTest {

    @Configuration
    @ComponentScan("com.olaleyeone.auth.service")
    static class $Config {

        @Bean
        public PasswordService passwordService() {
            return Mockito.mock(PasswordService.class);
        }

    }
}
