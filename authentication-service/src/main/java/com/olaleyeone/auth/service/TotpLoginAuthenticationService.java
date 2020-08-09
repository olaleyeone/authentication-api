package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.TotpLoginApiRequest;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.data.dto.RequestMetadata;

public interface TotpLoginAuthenticationService {

    PortalUserAuthentication getAuthenticationResponse(TotpLoginApiRequest requestDto, RequestMetadata requestMetadata);
}
