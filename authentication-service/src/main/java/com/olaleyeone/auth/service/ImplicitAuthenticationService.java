package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;

public interface ImplicitAuthenticationService {

    PortalUserAuthentication createSignUpAuthentication(PortalUser portalUser);

    PortalUserAuthentication createPasswordResetAuthentication(PasswordResetRequest passwordResetRequest);
}
