package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.entity.*;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.dto.UserRegistrationApiRequest;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierVerificationRepository;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Named
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final PortalUserRepository portalUserRepository;
    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PhoneNumberService phoneNumberService;
    private final HashService hashService;
    private final ImplicitAuthenticationService implicitAuthenticationService;
    private final Provider<TaskContext> taskContextProvider;
    private final PortalUserIdentifierVerificationRepository portalUserIdentifierVerificationRepository;
    private final PortalUserDataService portalUserDataService;

    @Activity("USER REGISTRATION")
    @Transactional
    @Override
    public PortalUserAuthentication registerUser(UserRegistrationApiRequest dto, RequestMetadata requestMetadata) {
        taskContextProvider.get().setDescription(String.format("Register user with email %s", dto.getEmail()));
        PortalUser portalUser = new PortalUser();
        portalUser.setFirstName(StringUtils.normalizeSpace(dto.getFirstName()));
        portalUser.setLastName(getNonEmptyString(dto.getLastName()));
        portalUser.setOtherName(getNonEmptyString(dto.getOtherName()));
        if (StringUtils.isNotBlank(dto.getPassword())) {
            portalUser.setPassword(hashService.generateHash(dto.getPassword()));
        }

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

        if (dto.getData() != null) {
            dto.getData().forEach(it -> {
                PortalUserData portalUserData = portalUserDataService.addData(portalUser, it);
                portalUserData.setCreatedBy(portalUser.getId().toString());
            });
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
        if (StringUtils.isNotBlank(dto.getEmailVerificationCode())) {
            taskContextProvider.get().execute(
                    "EMAIL VERIFICATION CODE VALIDATION",
                    String.format("Validate email verification code for %s", dto.getEmail()),
                    () -> resolveVerification(portalUserIdentifier, dto.getEmailVerificationCode()));
        }
        portalUserIdentifierRepository.save(portalUserIdentifier);
        return portalUserIdentifier;
    }

    private void resolveVerification(PortalUserIdentifier portalUserIdentifier, String verificationCode) {
        LocalDateTime now = LocalDateTime.now();
        List<PortalUserIdentifierVerification> list =
                portalUserIdentifierVerificationRepository.getAllActive(portalUserIdentifier.getIdentifier());
        if (list.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Iterator<PortalUserIdentifierVerification> iterator = list.iterator();
        PortalUserIdentifierVerification portalUserIdentifierVerification = iterator.next();
        if (!hashService.isSameHash(verificationCode, portalUserIdentifierVerification.getVerificationCodeHash())) {
            throw new IllegalArgumentException();
        }
        portalUserIdentifier.setVerified(true);
        portalUserIdentifier.setVerification(portalUserIdentifierVerification);

        portalUserIdentifierVerification.setUsedOn(now);
        portalUserIdentifierVerificationRepository.save(portalUserIdentifierVerification);

        while (iterator.hasNext()) {
            PortalUserIdentifierVerification next = iterator.next();
            next.setDeactivatedOn(now);
            portalUserIdentifierVerificationRepository.save(next);
        }
    }
}
