package com.olaleyeone.auth.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.configuration.AdditionalComponentsConfiguration;
import com.olaleyeone.auth.service.PhoneNumberService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

@ContextConfiguration(classes = {ControllerTest.$Config.class})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected Faker faker;

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
    }
}
