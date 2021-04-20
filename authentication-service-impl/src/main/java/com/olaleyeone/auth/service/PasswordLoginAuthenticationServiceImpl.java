package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.auth.data.dto.PasswordLoginApiRequest;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class PasswordLoginAuthenticationServiceImpl implements PasswordLoginAuthenticationService {

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final HashService hashService;
    private final LoginAuthenticationService loginAuthenticationService;

    @Activity("PROCESS USER LOGIN")
    @Transactional
    @Override
    public PortalUserAuthentication getAuthenticationResponse(PasswordLoginApiRequest apiRequest, RequestMetadata requestMetadata) {
        Optional<PortalUserIdentifier> optionalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(apiRequest.getIdentifier());
        if (!optionalUserIdentifier.isPresent()) {
            PortalUserAuthentication userAuthentication = loginAuthenticationService.makeAuthenticationResponse(
                    apiRequest,
                    requestMetadata,
                    AuthenticationResponseType.UNKNOWN_ACCOUNT,
                    optionalUserIdentifier);
            return loginAuthenticationService.createFailureResponse(apiRequest, userAuthentication);
        }
        PortalUserIdentifier userIdentifier = optionalUserIdentifier.get();
        if (StringUtils.isBlank(userIdentifier.getPortalUser().getPassword())
                || !hashService.isSameHash(apiRequest.getPassword(), userIdentifier.getPortalUser().getPassword())) {
            PortalUserAuthentication userAuthentication = loginAuthenticationService.makeAuthenticationResponse(
                    apiRequest,
                    requestMetadata,
                    AuthenticationResponseType.INCORRECT_CREDENTIAL,
                    optionalUserIdentifier);
            return loginAuthenticationService.createInvalidCredentialResponse(apiRequest, userAuthentication);
        }

        PortalUserAuthentication userAuthentication = loginAuthenticationService.makeAuthenticationResponse(
                apiRequest,
                requestMetadata,
                AuthenticationResponseType.SUCCESSFUL,
                optionalUserIdentifier);
        return loginAuthenticationService.createSuccessfulAuthenticationResponse(userAuthentication, apiRequest);
    }

}
