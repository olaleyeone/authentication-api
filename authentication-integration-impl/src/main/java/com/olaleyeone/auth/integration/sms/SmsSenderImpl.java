package com.olaleyeone.auth.integration.sms;

import com.github.olaleyeone.sms.api.Sms;
import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SmsSenderImpl implements SmsSender {

    private final com.github.olaleyeone.sms.api.SmsSender smsSender;

    @Override
    public void sendOtp(OneTimePassword oneTimePassword, String password) {
        smsSender.send(Sms.builder()
                .to(oneTimePassword.getUserIdentifier().getIdentifier())
                .message(String.format("Your passcode is %s", password))
                .build());
    }

    @Override
    public void sendVerificationCode(PortalUserIdentifierVerification identifierVerification, String code) {
        smsSender.send(Sms.builder()
                .to(identifierVerification.getIdentifier())
                .message(String.format("Your verification code is %s", code))
                .build());
    }
}
