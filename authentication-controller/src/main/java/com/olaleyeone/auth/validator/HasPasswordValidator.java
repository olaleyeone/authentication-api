package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.constraints.HasPassword;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import javax.inject.Provider;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
@Builder
@Named
public class HasPasswordValidator implements HasPassword.Validator {

    private final Provider<RequestMetadata> requestMetadataProvider;
    private final PortalUserRepository portalUserRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        PortalUser portalUser = portalUserRepository.findById(requestMetadataProvider.get().getPortalUserId()).get();
        if(BooleanUtils.isTrue(portalUser.getPasswordUpdateRequired())){
            return true;
        }
        return StringUtils.isNotBlank(value);
    }

}
