package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.data.LoginApiRequest;
import com.olaleyeone.auth.dto.data.RequestMetadata;
import com.olaleyeone.auth.repository.AuthenticationResponseRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final AuthenticationResponseRepository authenticationResponseRepository;
    private final PasswordService passwordService;

    @Transactional
    @Override
    public AuthenticationResponse getAuthenticationResponse(LoginApiRequest requestDto, RequestMetadata requestMetadata) {
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

    private AuthenticationResponse createUnknownAccountResponse(LoginApiRequest requestDto, RequestMetadata requestMetadata) {
        AuthenticationResponse authenticationResponse = makeAuthenticationResponse(requestDto, requestMetadata, AuthenticationResponseType.UNKNOWN_ACCOUNT);
        return authenticationResponseRepository.save(authenticationResponse);
    }

    private AuthenticationResponse createInvalidCredentialResponse(PortalUserIdentifier userIdentifier, LoginApiRequest requestDto, RequestMetadata requestMetadata) {
        AuthenticationResponse authenticationResponse = makeAuthenticationResponse(requestDto, requestMetadata, AuthenticationResponseType.INCORRECT_CREDENTIAL);
        authenticationResponse.setPortalUserIdentifier(userIdentifier);
        return authenticationResponseRepository.save(authenticationResponse);
    }

    private AuthenticationResponse createSuccessfulAuthenticationResponse(PortalUserIdentifier userIdentifier, LoginApiRequest requestDto, RequestMetadata requestMetadata) {
        AuthenticationResponse authenticationResponse = makeAuthenticationResponse(requestDto, requestMetadata, AuthenticationResponseType.SUCCESSFUL);
        authenticationResponse.setPortalUserIdentifier(userIdentifier);
        return authenticationResponseRepository.save(authenticationResponse);
    }

    private AuthenticationResponse makeAuthenticationResponse(LoginApiRequest requestDto, RequestMetadata requestMetadata, AuthenticationResponseType authenticationResponseType) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setResponseType(authenticationResponseType);
        authenticationResponse.setIdentifier(requestDto.getIdentifier());
        authenticationResponse.setIpAddress(requestMetadata.getIpAddress());
        authenticationResponse.setUserAgent(requestMetadata.getUserAgent());
        return authenticationResponse;
    }

}
