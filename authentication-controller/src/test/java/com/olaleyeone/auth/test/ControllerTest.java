package com.olaleyeone.auth.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.configuration.AdditionalComponentsConfiguration;
import com.olaleyeone.auth.configuration.BeanValidationConfiguration;
import com.olaleyeone.auth.configuration.SecurityConfiguration;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.security.access.AccessTokenValidator;
import com.olaleyeone.auth.security.access.TrustedIpAddressAccessManager;
import com.olaleyeone.auth.service.LoginAuthenticationService;
import com.olaleyeone.auth.service.PhoneNumberService;
import com.olaleyeone.auth.service.UserRegistrationService;
import com.olaleyeone.auth.validator.UniqueIdentifierValidator;
import com.olaleyeone.auth.validator.ValidPhoneNumberValidator;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.Random;

@ActiveProfiles("test")
@WebMvcTest
@ContextConfiguration(classes = {ControllerTest.$Config.class})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected Faker faker = Faker.instance(new Random());

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
            "com.olaleyeone.auth.advice"
    })
    @Import({
            AdditionalComponentsConfiguration.class,
            BeanValidationConfiguration.class,
            SecurityConfiguration.class
    })
    static class $Config {

        @Bean
        public PhoneNumberService phoneNumberService() {
            return Mockito.mock(PhoneNumberService.class);
        }

        @Bean
        public LoginAuthenticationService authenticationService() {
            return Mockito.mock(LoginAuthenticationService.class);
        }

        @Bean
        public UserRegistrationService userRegistrationService() {
            return Mockito.mock(UserRegistrationService.class);
        }

        @Bean
        public AccessTokenApiResponseHandler userApiResponseHandler() {
            return Mockito.mock(AccessTokenApiResponseHandler.class);
        }

        @Bean
        public UniqueIdentifierValidator uniqueIdentifierValidator() {
            return Mockito.mock(UniqueIdentifierValidator.class);
        }

        @Bean
        public ValidPhoneNumberValidator validPhoneNumberValidator() {
            return Mockito.mock(ValidPhoneNumberValidator.class);
        }

        @Bean
        public TrustedIpAddressAccessManager trustedIpAddressAccessManager() {
            return Mockito.mock(TrustedIpAddressAccessManager.class);
        }

        @Bean
        public AccessTokenValidator accessTokenValidator() {
            return Mockito.mock(AccessTokenValidator.class);
        }
    }
}
