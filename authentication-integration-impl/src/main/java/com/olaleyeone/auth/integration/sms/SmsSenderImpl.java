package com.olaleyeone.auth.integration.sms;

import com.github.olaleyeone.sms.api.Sms;
import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SmsSenderImpl implements SmsSender {

    private final com.github.olaleyeone.sms.api.SmsSender smsSender;
    private final Environment environment;

    @Override
    public void sendOtp(OneTimePassword oneTimePassword, String password) {
        smsSender.send(Sms.builder()
                .from(environment.getProperty("SMS_SENDER_NAME"))
                .to(oneTimePassword.getUserIdentifier().getIdentifier())
                .message(String.format("Your passcode is %s", password))
                .build());
    }

    @Override
    public void sendVerificationCode(PortalUserIdentifierVerification identifierVerification, String code) {
        smsSender.send(Sms.builder()
                .from(environment.getProperty("SMS_SENDER_NAME"))
                .to(identifierVerification.getIdentifier())
                .message(String.format("Your verification code is %s", code))
                .build());
    }

    @Override
    public void sendResetCode(PasswordResetRequest passwordResetRequest) {
        smsSender.send(Sms.builder()
                .from(environment.getProperty("SMS_SENDER_NAME"))
                .to(passwordResetRequest.getPortalUserIdentifier().getIdentifier())
                .message(String.format("Your password reset code is %s", passwordResetRequest.getResetCode()))
                .build());
    }
}
