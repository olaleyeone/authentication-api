package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.UserIdentifierType;

import java.util.Map;

public interface PortalUserIdentifierVerificationService {

    Map.Entry<PortalUserIdentifierVerification, String> createVerification(String identifier, UserIdentifierType identifierType);

    void applyVerification(PortalUserIdentifier userIdentifier, PortalUserIdentifierVerification userIdentifierVerification);
}
