package com.olaleyeone.auth.integration.etc;

public interface PhoneNumberService {

    String formatPhoneNumber(String phoneNumber);

    boolean isValid(String value);
}
