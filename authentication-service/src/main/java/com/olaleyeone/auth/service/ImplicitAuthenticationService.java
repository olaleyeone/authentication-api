package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.dto.data.RequestMetadata;

public interface ImplicitAuthenticationService {

    PortalUserAuthentication createSignUpAuthentication(PortalUser portalUser, RequestMetadata requestMetadata);
}
