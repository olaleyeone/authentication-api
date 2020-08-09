package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.PasswordLoginApiRequest;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.data.dto.RequestMetadata;

public interface PasswordLoginAuthenticationService {

    PortalUserAuthentication getAuthenticationResponse(PasswordLoginApiRequest requestDto, RequestMetadata requestMetadata);
}
