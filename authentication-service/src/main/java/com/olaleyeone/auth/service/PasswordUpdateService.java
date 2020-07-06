package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.PasswordResetApiRequest;
import com.olaleyeone.auth.data.dto.PasswordUpdateApiRequest;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;

import java.util.Optional;

public interface PasswordUpdateService {

    void updatePassword(RefreshToken portalUser, PasswordUpdateApiRequest passwordUpdateApiRequest);

    Optional<PortalUserAuthentication> updatePassword(PasswordResetRequest passwordResetRequest, PasswordResetApiRequest passwordUpdateApiRequest);
}
