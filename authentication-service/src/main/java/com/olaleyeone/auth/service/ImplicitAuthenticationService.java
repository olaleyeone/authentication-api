package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.UserRegistrationApiRequest;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;

public interface ImplicitAuthenticationService {

    PortalUserAuthentication createSignUpAuthentication(PortalUser portalUser, UserRegistrationApiRequest dto);

    PortalUserAuthentication createPasswordResetAuthentication(PasswordResetRequest passwordResetRequest);
}
