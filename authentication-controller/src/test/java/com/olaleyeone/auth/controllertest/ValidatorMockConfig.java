package com.olaleyeone.auth.controllertest;

import com.olaleyeone.auth.validator.UniqueIdentifierValidator;
import com.olaleyeone.auth.validator.ValidEmailRegistrationCodeValidator;
import com.olaleyeone.auth.validator.ValidPhoneNumberValidator;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ValidatorMockConfig {

    @Bean
    public UniqueIdentifierValidator uniqueIdentifierValidator() {
        return Mockito.mock(UniqueIdentifierValidator.class);
    }

    @Bean
    public ValidPhoneNumberValidator validPhoneNumberValidator() {
        return Mockito.mock(ValidPhoneNumberValidator.class);
    }

    @Bean
    public ValidEmailRegistrationCodeValidator validEmailRegistrationCodeValidator() {
        return Mockito.mock(ValidEmailRegistrationCodeValidator.class);
    }
}
