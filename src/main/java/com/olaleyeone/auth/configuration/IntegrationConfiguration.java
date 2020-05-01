package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.service.PhoneNumberService;
import com.olaleyeone.auth.service.PhoneNumberServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationConfiguration {

    @Bean
    public PhoneNumberService phoneNumberService() {
        return new PhoneNumberServiceImpl("US");
    }
}
