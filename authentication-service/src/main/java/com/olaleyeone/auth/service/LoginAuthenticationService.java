package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.dto.LoginApiRequest;
import com.olaleyeone.data.dto.RequestMetadata;

public interface LoginAuthenticationService {

    PortalUserAuthentication getAuthenticationResponse(LoginApiRequest requestDto, RequestMetadata requestMetadata);
}
