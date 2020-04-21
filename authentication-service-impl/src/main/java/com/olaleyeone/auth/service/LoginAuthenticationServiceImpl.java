package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.auth.dto.data.LoginApiRequest;
import com.olaleyeone.auth.dto.data.RequestMetadata;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class LoginAuthenticationServiceImpl implements LoginAuthenticationService {

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;
    private final PasswordService passwordService;

    @Transactional
    @Override
    public PortalUserAuthentication getAuthenticationResponse(LoginApiRequest requestDto, RequestMetadata requestMetadata) {
        Optional<PortalUserIdentifier> optionalUserIdentifier = portalUserIdentifierRepository.findByIdentifier(requestDto.getIdentifier());
        if (!optionalUserIdentifier.isPresent()) {
            return createUnknownAccountResponse(requestDto, requestMetadata);
        }
        PortalUserIdentifier userIdentifier = optionalUserIdentifier.get();
        if (!passwordService.isSameHash(requestDto.getPassword(), userIdentifier.getPortalUser().getPassword())) {
            return createInvalidCredentialResponse(userIdentifier, requestDto, requestMetadata);
        }
        return createSuccessfulAuthenticationResponse(userIdentifier, requestDto, requestMetadata);
    }

    private PortalUserAuthentication createUnknownAccountResponse(LoginApiRequest requestDto, RequestMetadata requestMetadata) {
        PortalUserAuthentication userAuthentication = makeAuthenticationResponse(requestDto, requestMetadata, AuthenticationResponseType.UNKNOWN_ACCOUNT);
        return portalUserAuthenticationRepository.save(userAuthentication);
    }

    private PortalUserAuthentication createInvalidCredentialResponse(PortalUserIdentifier userIdentifier, LoginApiRequest requestDto, RequestMetadata requestMetadata) {
        PortalUserAuthentication userAuthentication = makeAuthenticationResponse(requestDto, requestMetadata, AuthenticationResponseType.INCORRECT_CREDENTIAL);
        userAuthentication.setPortalUserIdentifier(userIdentifier);
        return portalUserAuthenticationRepository.save(userAuthentication);
    }

    private PortalUserAuthentication createSuccessfulAuthenticationResponse(PortalUserIdentifier userIdentifier, LoginApiRequest requestDto, RequestMetadata requestMetadata) {
        PortalUserAuthentication userAuthentication = makeAuthenticationResponse(requestDto, requestMetadata, AuthenticationResponseType.SUCCESSFUL);
        userAuthentication.setPortalUserIdentifier(userIdentifier);
        return portalUserAuthenticationRepository.save(userAuthentication);
    }

    private PortalUserAuthentication makeAuthenticationResponse(LoginApiRequest requestDto, RequestMetadata requestMetadata, AuthenticationResponseType authenticationResponseType) {
        PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
        userAuthentication.setType(AuthenticationType.LOGIN);
        userAuthentication.setResponseType(authenticationResponseType);
        userAuthentication.setIdentifier(requestDto.getIdentifier());
        userAuthentication.setIpAddress(requestMetadata.getIpAddress());
        userAuthentication.setUserAgent(requestMetadata.getUserAgent());
        return userAuthentication;
    }

}
