package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.constraints.HasName;
import com.olaleyeone.auth.data.dto.UserRegistrationApiRequest;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import javax.validation.ConstraintValidatorContext;

@Named
public class HasNameValidator implements HasName.Validator {

    @Override
    public boolean isValid(UserRegistrationApiRequest value, ConstraintValidatorContext context) {
        return !StringUtils.isAllBlank(value.getDisplayName(), value.getFirstName());
    }

}
