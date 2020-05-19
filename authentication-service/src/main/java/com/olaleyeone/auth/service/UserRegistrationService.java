package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.data.dto.RequestMetadata;
import com.olaleyeone.auth.dto.UserRegistrationApiRequest;

public interface UserRegistrationService {

    PortalUserAuthentication registerUser(UserRegistrationApiRequest dto, RequestMetadata requestMetadata);
}
