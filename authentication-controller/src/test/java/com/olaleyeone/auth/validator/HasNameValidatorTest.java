package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.dto.UserRegistrationApiRequest;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HasNameValidatorTest extends ComponentTest {

    private HasNameValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new HasNameValidator();
    }

    @Test
    void isInValid() {
        assertFalse(validator.isValid(new UserRegistrationApiRequest(), null));
    }

    @Test
    void isValidWithDisplayName() {
        UserRegistrationApiRequest registrationApiRequest = new UserRegistrationApiRequest();
        registrationApiRequest.setDisplayName(faker.lordOfTheRings().character());
        assertTrue(validator.isValid(registrationApiRequest, null));
    }

    @Test
    void isValidWithFirstName() {
        UserRegistrationApiRequest registrationApiRequest = new UserRegistrationApiRequest();
        registrationApiRequest.setFirstName(faker.lordOfTheRings().character());
        assertTrue(validator.isValid(registrationApiRequest, null));
    }

    @Test
    void isValid() {
        UserRegistrationApiRequest registrationApiRequest = new UserRegistrationApiRequest();
        registrationApiRequest.setFirstName(faker.lordOfTheRings().character());
        registrationApiRequest.setDisplayName(faker.lordOfTheRings().character());
        assertTrue(validator.isValid(registrationApiRequest, null));
    }
}