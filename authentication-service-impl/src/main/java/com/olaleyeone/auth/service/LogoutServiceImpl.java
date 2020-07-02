package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Named
public class LogoutServiceImpl implements LogoutService {

    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;

    @Activity("LOGOUT")
    @Transactional
    @Override
    public void logout(PortalUserAuthentication portalUserAuthentication) {
        portalUserAuthentication.setLoggedOutAt(OffsetDateTime.now());
        portalUserAuthenticationRepository.save(portalUserAuthentication);
    }
}
