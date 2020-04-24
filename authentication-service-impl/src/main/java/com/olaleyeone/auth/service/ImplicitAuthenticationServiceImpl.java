package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.ActivityLogger;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.data.RequestMetadata;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;

@Named
@RequiredArgsConstructor
public class ImplicitAuthenticationServiceImpl implements ImplicitAuthenticationService {

    private final Provider<ActivityLogger> activityLoggerProvider;
    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;

    @Transactional
    @Override
    public PortalUserAuthentication createSignUpAuthentication(PortalUser portalUser, RequestMetadata requestMetadata) {
        activityLoggerProvider.get().log("LOGIN",
                String.format("Auto login for user %s after registration", portalUser.getId()));
        PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
        userAuthentication.setPortalUser(portalUser);
        userAuthentication.setType(AuthenticationType.USER_REGISTRATION);
        userAuthentication.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        userAuthentication.setIpAddress(requestMetadata.getIpAddress());
        userAuthentication.setUserAgent(requestMetadata.getUserAgent());
        return portalUserAuthenticationRepository.save(userAuthentication);
    }

}
