package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.PasswordResetRequestRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class PasswordResetRequestServiceImpl implements PasswordResetRequestService {

    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final SettingService settingService;
    private final HashService hashService;
    private final Provider<TaskContext> taskContextProvider;

    private final Random random = new Random();

    private boolean saveResetCode = true;

    @Activity("PASSWORD RESET REQUEST")
    @Transactional
    @Override
    public Map.Entry<PasswordResetRequest, String> createRequest(PortalUserIdentifier portalUserIdentifier) {
        LocalDateTime now = LocalDateTime.now();
        taskContextProvider.get().setDescription(String.format("Generate password reset code for %s [%s]",
                portalUserIdentifier.getIdentifier(), portalUserIdentifier.getIdentifierType()));


        PasswordResetRequest portalUserIdentifierVerification = new PasswordResetRequest();
        portalUserIdentifierVerification.setPortalUserIdentifier(portalUserIdentifier);

        taskContextProvider.get().execute(
                "EXISTING VERIFICATION CODE DEACTIVATION",
                () -> {
                    List<PasswordResetRequest> allActive =
                            passwordResetRequestRepository.getAllActive(portalUserIdentifier.getPortalUser());
                    taskContextProvider.get().setDescription(String.format("Deactivated %d existing verification code(s) for user %s",
                            allActive.size(), portalUserIdentifier.getPortalUser().getId()));
                    allActive.forEach(verification -> {
                        verification.setDeactivatedOn(now);
                        passwordResetRequestRepository.save(verification);
                    });
                });

        portalUserIdentifierVerification.setCreatedOn(now);
        int duration = settingService.getInteger("PASSWORD_RESET_CODE_EXPIRY_PERIOD_IN_MINUTES", 15);
        portalUserIdentifierVerification.setExpiresOn(now.plusMinutes(duration));

        String verificationCode = generateVerificationCode();
        if (saveResetCode) {
            portalUserIdentifierVerification.setResetCode(verificationCode);
        }
        portalUserIdentifierVerification.setResetCodeHash(hashService.generateHash(verificationCode));
        passwordResetRequestRepository.save(portalUserIdentifierVerification);
        return Pair.of(portalUserIdentifierVerification, verificationCode);
    }

    private String generateVerificationCode() {
        StringBuilder builder = new StringBuilder();
        int tokenLength = settingService.getInteger("PASSWORD_RESET_CODE_LENGTH", 6);
        for (int i = 0; i < tokenLength; i++) {
            builder.append(Math.round(random.nextDouble() * 9));
        }
        return builder.toString();
    }
}
