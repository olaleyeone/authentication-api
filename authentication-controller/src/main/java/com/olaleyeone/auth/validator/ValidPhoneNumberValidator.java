package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.dto.constraints.ValidPhoneNumber;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
@Named
public class ValidPhoneNumberValidator implements ValidPhoneNumber.Validator {

    private final PhoneNumberService phoneNumberService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isBlank(value)) {
            return true;
        }

        return phoneNumberService.isValid(value);
    }

}
