package com.olaleyeone.auth.servicetest;

import com.olaleyeone.auth.entitytest.EntityTest;
import com.olaleyeone.auth.service.PasswordService;
import com.olaleyeone.auth.service.PhoneNumberService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.internal.creation.bytebuddy.MockAccess;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ServiceTest.$Config.class, TestAuditTrailConfiguration.class})
public class ServiceTest extends EntityTest {

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

        @Bean
        public PhoneNumberService phoneNumberService() {
            return Mockito.mock(PhoneNumberService.class);
        }

    }
}
