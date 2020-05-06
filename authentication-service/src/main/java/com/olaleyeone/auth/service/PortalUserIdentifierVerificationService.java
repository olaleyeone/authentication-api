package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.UserIdentifierType;

import java.util.Map;

public interface PortalUserIdentifierVerificationService {

    Map.Entry<PortalUserIdentifierVerification, String> createVerification(String identifier, UserIdentifierType identifierType);

    boolean confirmVerification(PortalUserIdentifierVerification verification, String verificationCode);
}
