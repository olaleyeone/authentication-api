package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.dto.constraints.UniqueIdentifier;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.PhoneNumberService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
@Scope(DefaultListableBeanFactory.SCOPE_PROTOTYPE)
@Named
public class UniqueIdentifierValidator implements UniqueIdentifier.Validator {

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PhoneNumberService phoneNumberService;

    private UserIdentifierType identifierType;

    @Override
    public void initialize(UniqueIdentifier constraintAnnotation) {
        this.identifierType = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        if (identifierType == UserIdentifierType.PHONE_NUMBER) {
            value = phoneNumberService.formatPhoneNumber(value);
        }
        return !portalUserIdentifierRepository.findByIdentifier(value).isPresent();
    }
}
