package com.olaleyeone.auth.service;

import com.google.i18n.phonenumbers.NumberParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberServiceImplTest {

    private PhoneNumberServiceImpl phoneNumberService;

    @BeforeEach
    public void init() {
        phoneNumberService = new PhoneNumberServiceImpl("US");
    }

    @Test
    void formatPhoneNumber() {
        assertEquals("+15417543010", phoneNumberService.formatPhoneNumber("15417543010"));
    }

    @Test
    void formatPhoneNumberWithInvalidInput() {
        assertEquals("+180341", phoneNumberService.formatPhoneNumber("80341"));
    }

    @Test
    void formatPhoneNumberWithNullInput() {
        assertNull(phoneNumberService.formatPhoneNumber(null));
    }

    @Test
    void formatPhoneNumberWithBadInput() {
        assertThrows(NumberParseException.class, () -> phoneNumberService.formatPhoneNumber("jdhfhfh"));
    }

    @Test
    void isValid() {
        assertTrue(phoneNumberService.isValid("15417543010"));
    }

    @Test
    void isValidWithInvalidInput() {
        assertTrue(phoneNumberService.isValid("8034120919"));
    }

    @Test
    void isValidWithBadInput() {
        assertFalse(phoneNumberService.isValid("adffdfd"));
    }

    @Test
    void isValidWithNullInput() {
        assertTrue(phoneNumberService.isValid(null));
    }
}
