package com.olaleyeone.auth.integration.sms;


import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;

public interface SmsSender {

    void sendOtp(OneTimePassword oneTimePassword, String password);

    void sendVerificationCode(PortalUserIdentifierVerification identifierVerification, String code);
}
