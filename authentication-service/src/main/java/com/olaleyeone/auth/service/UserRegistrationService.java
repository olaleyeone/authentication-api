package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.data.RequestMetadata;
import com.olaleyeone.auth.dto.data.UserRegistrationApiRequest;

public interface UserRegistrationService {

    PortalUserAuthentication registerUser(UserRegistrationApiRequest dto, RequestMetadata requestMetadata);
}
