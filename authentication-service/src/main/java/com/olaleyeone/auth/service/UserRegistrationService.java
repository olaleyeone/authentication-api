package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.dto.data.UserRegistrationApiRequest;

public interface UserRegistrationService {

    PortalUser registerUser(UserRegistrationApiRequest dto);
}
