package com.olaleyeone.auth.test;

import com.olaleyeone.auth.service.LoginAuthenticationService;
import com.olaleyeone.auth.service.PhoneNumberService;
import com.olaleyeone.auth.service.UserRegistrationService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ServiceMockConfig {

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
}
