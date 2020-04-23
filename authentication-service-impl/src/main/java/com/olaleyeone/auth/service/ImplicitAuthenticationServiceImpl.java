package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.data.RequestMetadata;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@RequiredArgsConstructor
public class ImplicitAuthenticationServiceImpl implements ImplicitAuthenticationService {

    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;

    @Transactional
    @Override
    public PortalUserAuthentication createSignUpAuthentication(PortalUser portalUser, RequestMetadata requestMetadata) {
        PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
        userAuthentication.setPortalUser(portalUser);
        userAuthentication.setType(AuthenticationType.USER_REGISTRATION);
        userAuthentication.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        userAuthentication.setIpAddress(requestMetadata.getIpAddress());
        userAuthentication.setUserAgent(requestMetadata.getUserAgent());
        return portalUserAuthenticationRepository.save(userAuthentication);
    }

}
