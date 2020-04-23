package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.service.PhoneNumberService;
import com.olaleyeone.auth.service.PhoneNumberServiceImpl;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.security.Key;

@Configuration
public class IntegrationConfiguration {

    @Bean
    @Scope(DefaultListableBeanFactory.SCOPE_PROTOTYPE)
    public Key jwtEncryptionKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    @Bean
    public PhoneNumberService phoneNumberService() {
        return new PhoneNumberServiceImpl("US");
    }
}
