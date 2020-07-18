package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;

public interface LogoutService {

    void logout(PortalUserAuthentication portalUserAuthentication);
}
