package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.constraints.HasName;
import com.olaleyeone.auth.dto.UserRegistrationApiRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
@Named
public class HasNameValidator implements HasName.Validator {

    @Override
    public boolean isValid(UserRegistrationApiRequest value, ConstraintValidatorContext context) {
        return !StringUtils.isAllBlank(value.getDisplayName(), value.getFirstName());
    }

}
