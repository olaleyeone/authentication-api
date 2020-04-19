package com.olaleyeone.auth.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.configuration.AdditionalComponentsConfiguration;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.service.AuthenticationService;
import com.olaleyeone.auth.service.PhoneNumberService;
import com.olaleyeone.auth.service.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.internal.creation.bytebuddy.MockAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.Random;

@WebMvcTest
@ContextConfiguration(classes = {ControllerTest.$Config.class})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected Faker faker;

    @Inject
    private ApplicationContext applicationContext;

    @BeforeEach
    public void resetMocks() {
        applicationContext.getBeansOfType(MockAccess.class)
                .values().forEach(Mockito::reset);
    }

    @Configuration
    @ComponentScan({
            "com.olaleyeone.auth.controller",
            "com.olaleyeone.auth.advice",
            "com.olaleyeone.auth.validator"
    })
    @Import(AdditionalComponentsConfiguration.class)
    static class $Config {

        @Bean
        public Faker faker() {
            return Faker.instance(new Random());
        }

        @Bean
        public PhoneNumberService phoneNumberService() {
            return Mockito.mock(PhoneNumberService.class);
        }

        @Bean
        public AuthenticationService authenticationService() {
            return Mockito.mock(AuthenticationService.class);
        }

        @Bean
        public UserRegistrationService userRegistrationService() {
            return Mockito.mock(UserRegistrationService.class);
        }

        @Bean
        public UserApiResponseHandler userPojoHandler() {
            return Mockito.mock(UserApiResponseHandler.class);
        }
    }
}
