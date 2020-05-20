package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.constraints.ValidEmailVerificationCode;
import com.olaleyeone.auth.dto.UserRegistrationApiRequest;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.PortalUserIdentifierVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@RequiredArgsConstructor
@Named
public class ValidEmailRegistrationCodeValidator implements ValidEmailVerificationCode.Validator {

    private final PortalUserIdentifierVerificationRepository portalUserIdentifierVerificationRepository;
    private final HashService hashService;

    @Override
    public boolean isValid(UserRegistrationApiRequest value, ConstraintValidatorContext context) {

        if (StringUtils.isBlank(value.getEmailVerificationCode())) {
            return true;
        }

        List<PortalUserIdentifierVerification> list = portalUserIdentifierVerificationRepository.getAllActive(value.getEmail());
        if (list.isEmpty()) {
            return false;
        }
        return hashService.isSameHash(value.getEmailVerificationCode(), list.iterator().next().getVerificationCodeHash());
    }

}
