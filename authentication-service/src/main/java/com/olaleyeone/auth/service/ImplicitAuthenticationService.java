package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.data.RequestMetadata;

public interface ImplicitAuthenticationService {

    PortalUserAuthentication createSignUpAuthentication(PortalUser portalUser, RequestMetadata requestMetadata);
}
