package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.etc.HashService;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.repository.PortalUserIdentifierVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@Named
public class PortalUserIdentifierVerificationServiceImpl implements PortalUserIdentifierVerificationService {

    private final PortalUserIdentifierVerificationRepository portalUserIdentifierVerificationRepository;
    private final SettingService settingService;
    private final PhoneNumberService phoneNumberService;
    private final HashService hashService;

    private final Random random = new Random();

    private boolean saveVerificationCode = true;

    @Transactional
    @Override
    public Map.Entry<PortalUserIdentifierVerification, String> createVerification(String identifierArg, UserIdentifierType identifierType) {

        LocalDateTime now = LocalDateTime.now();
        String identifier = identifierArg;
        if (identifierType == UserIdentifierType.PHONE_NUMBER) {
            identifier = phoneNumberService.formatPhoneNumber(identifier);
        }

        portalUserIdentifierVerificationRepository.getAllActive(identifier)
                .forEach(verification -> {
                    verification.setDeactivatedOn(now);
                    portalUserIdentifierVerificationRepository.save(verification);
                });
        PortalUserIdentifierVerification portalUserIdentifierVerification = new PortalUserIdentifierVerification();
        portalUserIdentifierVerification.setIdentifierType(identifierType);
        portalUserIdentifierVerification.setIdentifier(identifier);
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

    @Transactional
    @Override
    public boolean confirmVerification(PortalUserIdentifierVerification verification, String verificationCode) {
        if (verification.getExpiresOn().isBefore(LocalDateTime.now())) {
            return false;
        }
        if (verification.getUsedOn() != null) {
            return false;
        }
        if (!hashService.isSameHash(verificationCode, verification.getVerificationCodeHash())) {
            return false;
        }
        verification.setUsedOn(LocalDateTime.now());
        portalUserIdentifierVerificationRepository.save(verification);
        return true;
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
