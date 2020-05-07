package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;

public interface LogoutService {

    void logout(PortalUserAuthentication portalUserAuthentication);
}
