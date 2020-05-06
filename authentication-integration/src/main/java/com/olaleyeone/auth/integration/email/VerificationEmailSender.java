package com.olaleyeone.auth.integration.email;


import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;

public interface VerificationEmailSender {

    void sendVerificationCode(PortalUserIdentifierVerification user, String verificationCode) throws Exception;
}
