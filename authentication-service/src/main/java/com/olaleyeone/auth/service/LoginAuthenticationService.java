package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.dto.data.LoginApiRequest;
import com.olaleyeone.auth.dto.data.RequestMetadata;

public interface LoginAuthenticationService {

    PortalUserAuthentication getAuthenticationResponse(LoginApiRequest requestDto, RequestMetadata requestMetadata);
}
