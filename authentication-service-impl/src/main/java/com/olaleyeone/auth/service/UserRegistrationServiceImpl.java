package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.ActivityLogger;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.data.RequestMetadata;
import com.olaleyeone.auth.dto.data.UserRegistrationApiRequest;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.repository.PortalUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Named
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final PortalUserRepository portalUserRepository;
    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PhoneNumberService phoneNumberService;
    private final PasswordService passwordService;
    private final ImplicitAuthenticationService implicitAuthenticationService;
    private final Provider<ActivityLogger> activityLoggerProvider;

    @Transactional
    @Override
    public PortalUserAuthentication registerUser(UserRegistrationApiRequest dto, RequestMetadata requestMetadata) {
        activityLoggerProvider.get().log("USER REGISTRATION", String.format("Registering user with email %s", dto.getEmail()));
        PortalUser portalUser = new PortalUser();
        portalUser.setFirstName(StringUtils.normalizeSpace(dto.getFirstName()));
        portalUser.setLastName(getNonEmptyString(dto.getLastName()));
        portalUser.setOtherName(getNonEmptyString(dto.getOtherName()));
        portalUser.setPassword(passwordService.hashPassword(dto.getPassword()));

        portalUserRepository.save(portalUser);

        List<PortalUserIdentifier> userIdentifiers = new ArrayList<>();

        if (StringUtils.isNotBlank(dto.getEmail())) {
            userIdentifiers.add(createEmailIdentifier(portalUser, dto));
        }

        if (StringUtils.isNotBlank(dto.getPhoneNumber())) {
            userIdentifiers.add(createPhoneNumberIdentifier(portalUser, dto));
        }

        if (userIdentifiers.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return implicitAuthenticationService.createSignUpAuthentication(portalUser, requestMetadata);
    }

    private String getNonEmptyString(String value) {
        return Optional.ofNullable(value).map(StringUtils::normalizeSpace)
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }

    private PortalUserIdentifier createPhoneNumberIdentifier(PortalUser portalUser, UserRegistrationApiRequest dto) {
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        portalUserIdentifier.setIdentifier(phoneNumberService.formatPhoneNumber(dto.getPhoneNumber()));
        portalUserIdentifier.setIdentifierType(UserIdentifierType.PHONE_NUMBER);
        portalUserIdentifier.setPortalUser(portalUser);
        return portalUserIdentifierRepository.save(portalUserIdentifier);
    }

    private PortalUserIdentifier createEmailIdentifier(PortalUser portalUser, UserRegistrationApiRequest dto) {
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        portalUserIdentifier.setIdentifier(dto.getEmail().trim());
        portalUserIdentifier.setIdentifierType(UserIdentifierType.EMAIL);
        portalUserIdentifier.setPortalUser(portalUser);
        return portalUserIdentifierRepository.save(portalUserIdentifier);
    }
}
