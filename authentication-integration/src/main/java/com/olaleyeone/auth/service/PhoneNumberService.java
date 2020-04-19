package com.olaleyeone.auth.service;

public interface PhoneNumberService {

    String formatPhoneNumber(String phoneNumber);

    boolean isValid(String value);
}
