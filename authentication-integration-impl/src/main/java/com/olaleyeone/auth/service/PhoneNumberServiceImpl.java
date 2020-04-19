package com.olaleyeone.auth.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class PhoneNumberServiceImpl implements PhoneNumberService {

    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private final String defaultRegion;

    public String formatPhoneNumber(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return null;
        }
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber.replaceAll(" +", ""), defaultRegion);
            return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValid(String value) {

        if (value == null) {
            return true;
        }

        Phonenumber.PhoneNumber swissNumberProto;
        try {
            swissNumberProto = phoneNumberUtil.parse(value.trim(), "US");
        } catch (NumberParseException e) {
            return false;
        }

        return phoneNumberUtil.isValidNumber(swissNumberProto);
    }
}
