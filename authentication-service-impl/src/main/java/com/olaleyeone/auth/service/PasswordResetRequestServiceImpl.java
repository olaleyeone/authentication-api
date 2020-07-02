package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.PasswordResetRequestRepository;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;
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
    private final Provider<RequestMetadata> requestMetadataProvider;

    private final Random random = new Random();

    private boolean saveResetCode = true;

    @Activity("PASSWORD RESET REQUEST")
    @Transactional
    @Override
    public Map.Entry<PasswordResetRequest, String> createRequest(PortalUserIdentifier portalUserIdentifier) {
        RequestMetadata requestMetadata = requestMetadataProvider.get();
        OffsetDateTime now = OffsetDateTime.now();
        taskContextProvider.get().setDescription(String.format("Generate password reset code for %s [%s]",
                portalUserIdentifier.getIdentifier(), portalUserIdentifier.getIdentifierType()));

        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setPortalUserIdentifier(portalUserIdentifier);
        passwordResetRequest.setIpAddress(requestMetadata.getIpAddress());
        passwordResetRequest.setUserAgent(requestMetadata.getUserAgent());

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

        passwordResetRequest.setCreatedOn(now);
        int duration = settingService.getInteger("PASSWORD_RESET_CODE_EXPIRY_PERIOD_IN_MINUTES", 15);
        passwordResetRequest.setExpiresOn(now.plusMinutes(duration));

        String verificationCode = generateVerificationCode();
        if (saveResetCode) {
            passwordResetRequest.setResetCode(verificationCode);
        }
        passwordResetRequest.setResetCodeHash(hashService.generateHash(verificationCode));
        passwordResetRequestRepository.save(passwordResetRequest);
        return Pair.of(passwordResetRequest, verificationCode);
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
