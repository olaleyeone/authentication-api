package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;

import java.util.Map;

public interface PasswordResetRequestService {

    Map.Entry<PasswordResetRequest, String> createRequest(PortalUserIdentifier portalUserIdentifier, boolean autoLogin);
}
