package com.olaleyeone.auth.integration.email;


import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;

import java.util.Map;

public interface VerificationEmailSender {

    void sendVerificationCode(PortalUserIdentifierVerification user, String verificationCode, Map<String, Object> parameters) throws Exception;
}
