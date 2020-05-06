package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.etc.HashService;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.repository.PortalUserIdentifierVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@Named
public class PortalUserIdentifierVerificationServiceImpl implements PortalUserIdentifierVerificationService {

    private final PortalUserIdentifierVerificationRepository portalUserIdentifierVerificationRepository;
    private final SettingService settingService;
    private final PhoneNumberService phoneNumberService;
    private final HashService hashService;
    private final Provider<TaskContext> taskContextProvider;

    private final Random random = new Random();

    private boolean saveVerificationCode = true;

    @Activity("VERIFICATION CODE GENERATION")
    @Transactional
    @Override
    public Map.Entry<PortalUserIdentifierVerification, String> createVerification(String identifierArg, UserIdentifierType identifierType) {
        LocalDateTime now = LocalDateTime.now();
        String identifier = identifierArg;
        if (identifierType == UserIdentifierType.PHONE_NUMBER) {
            identifier = phoneNumberService.formatPhoneNumber(identifier);
        }
        taskContextProvider.get().setDescription(String.format("Generate verification code for %s [%s]",
                identifierArg, identifierType.name()));


        PortalUserIdentifierVerification portalUserIdentifierVerification = new PortalUserIdentifierVerification();
        portalUserIdentifierVerification.setIdentifierType(identifierType);
        portalUserIdentifierVerification.setIdentifier(identifier);

        taskContextProvider.get().execute(
                "EXISTING VERIFICATION CODE DEACTIVATION",
                () -> {
                    List<PortalUserIdentifierVerification> allActive =
                            portalUserIdentifierVerificationRepository.getAllActive(portalUserIdentifierVerification.getIdentifier());
                    taskContextProvider.get().setDescription(String.format("Deactivated %d existing verification code(s) for %s [%s]",
                            allActive.size(), identifierArg, identifierType.name()));
                    allActive.forEach(verification -> {
                        verification.setDeactivatedOn(now);
                        portalUserIdentifierVerificationRepository.save(verification);
                    });
                    return null;
                });

        portalUserIdentifierVerification.setCreatedOn(now);
        int duration = settingService.getInteger("IDENTIFIER_VERIFICATION_CODE_EXPIRY_PERIOD_IN_SECONDS", 300);
        portalUserIdentifierVerification.setExpiresOn(now.plusSeconds(duration));

        String verificationCode = generateVerificationCode();
        if (saveVerificationCode) {
            portalUserIdentifierVerification.setVerificationCode(verificationCode);
        }
        portalUserIdentifierVerification.setVerificationCodeHash(hashService.generateHash(verificationCode));
        portalUserIdentifierVerificationRepository.save(portalUserIdentifierVerification);
        return Pair.of(portalUserIdentifierVerification, verificationCode);
    }

    private String generateVerificationCode() {
        StringBuilder builder = new StringBuilder();
        int tokenLength = settingService.getInteger("IDENTIFIER_VERIFICATION_CODE_LENGTH", 6);
        for (int i = 0; i < tokenLength; i++) {
            builder.append(Math.round(random.nextDouble() * 9));
        }
        return builder.toString();
    }
}
