package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.OneTimePasswordRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@Named
public class OneTimePasswordServiceImpl implements OneTimePasswordService {

    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final SettingService settingService;
    private final HashService hashService;
    private final Provider<TaskContext> taskContextProvider;

    private final Random random = new Random();

    private boolean saveVerificationCode = true;

    @Activity("GENERATE OTP")
    @Transactional
    @Override
    public Map.Entry<OneTimePassword, String> createOTP(PortalUserIdentifier identifier) {
        String password = generateVerificationCode();
        return Pair.of(createOTP(identifier, password), password);
    }

    @Activity("GENERATE OTP")
    @Transactional
    @Override
    public OneTimePassword createOTP(PortalUserIdentifier identifier, String password) {
        OffsetDateTime now = OffsetDateTime.now();
        taskContextProvider.get().setDescription(String.format("Generate OTP for identifier %d",
                identifier.getId()));

        OneTimePassword oneTimePassword = new OneTimePassword();
        oneTimePassword.setUserIdentifier(identifier);

        taskContextProvider.get().execute(
                "EXISTING OTP DEACTIVATION",
                () -> {
                    List<OneTimePassword> allActive =
                            oneTimePasswordRepository.getAllActive(oneTimePassword.getUserIdentifier());
                    taskContextProvider.get().setDescription(String.format("Deactivated %d existing OTPs for identifier %d",
                            allActive.size(), identifier.getId()));
                    allActive.forEach(verification -> {
                        verification.setDeactivatedAt(now);
                        oneTimePasswordRepository.save(verification);
                    });
                });

        oneTimePassword.setCreatedAt(now);
        int duration = settingService.getInteger("OTP_EXPIRY_PERIOD_IN_SECONDS", 600);
        oneTimePassword.setExpiresAt(now.plusSeconds(duration));

        if (saveVerificationCode) {
            oneTimePassword.setPassword(password);
        }
        oneTimePassword.setHash(hashService.generateHash(password));
        oneTimePasswordRepository.save(oneTimePassword);
        return oneTimePassword;
    }

    private String generateVerificationCode() {
        StringBuilder builder = new StringBuilder();
        int tokenLength = settingService.getInteger("OTP_LENGTH", 6);
        for (int i = 0; i < tokenLength; i++) {
            builder.append(Math.round(random.nextDouble() * 9));
        }
        return builder.toString();
    }
}