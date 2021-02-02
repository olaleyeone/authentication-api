package com.olaleyeone.auth.integration.sms;


import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;

public interface SmsSender {

    void sendOtp(OneTimePassword oneTimePassword, String password);

    void sendVerificationCode(PortalUserIdentifierVerification identifierVerification, String code);

    void sendResetCode(PasswordResetRequest passwordResetRequest);
}
