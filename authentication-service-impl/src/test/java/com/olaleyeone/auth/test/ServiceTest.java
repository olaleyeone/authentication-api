package com.olaleyeone.auth.test;

import com.olaleyeone.auth.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.internal.creation.bytebuddy.MockAccess;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Arrays;

@Import(ServiceTest.$Config.class)
public class ServiceTest extends EntityTest {

    @Inject
    private ApplicationContext applicationContext;

    @BeforeEach
    public void resetMocks() {
        applicationContext.getBeansOfType(MockAccess.class)
                .values().forEach(Mockito::reset);
    }

    @Configuration
    @ComponentScan("com.olaleyeone.auth.service")
    static class $Config {

        @Bean
        public PasswordService passwordService() {
            return Mockito.mock(PasswordService.class);
        }

    }
}
