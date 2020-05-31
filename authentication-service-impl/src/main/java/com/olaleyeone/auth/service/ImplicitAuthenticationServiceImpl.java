package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;

@Named
@RequiredArgsConstructor
public class ImplicitAuthenticationServiceImpl implements ImplicitAuthenticationService {

    private final Provider<TaskContext> activityLoggerProvider;
    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;

    @Activity("LOGIN")
    @Transactional
    @Override
    public PortalUserAuthentication createSignUpAuthentication(PortalUser portalUser, RequestMetadata requestMetadata) {
        activityLoggerProvider.get().setDescription(
                String.format("Auto login user %s after registration", portalUser.getId()));
        PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
        userAuthentication.setPortalUser(portalUser);
        userAuthentication.setType(AuthenticationType.USER_REGISTRATION);
        userAuthentication.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        userAuthentication.setIpAddress(requestMetadata.getIpAddress());
        userAuthentication.setUserAgent(requestMetadata.getUserAgent());
        return portalUserAuthenticationRepository.save(userAuthentication);
    }

}
