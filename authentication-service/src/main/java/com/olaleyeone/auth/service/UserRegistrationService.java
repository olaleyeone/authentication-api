package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.UserRegistrationApiRequest;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;

public interface UserRegistrationService {

    PortalUserAuthentication registerUser(UserRegistrationApiRequest dto);
}
