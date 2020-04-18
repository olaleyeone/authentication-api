package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.UserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.LoginRequestDto;
import com.olaleyeone.auth.dto.RequestMetadata;
import com.olaleyeone.auth.repository.AuthenticationResponseRepository;
import com.olaleyeone.auth.repository.UserIdentifierRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserIdentifierRepository userIdentifierRepository;
    private final AuthenticationResponseRepository authenticationResponseRepository;
    private final PasswordService passwordService;

    @Transactional
    @Override
    public AuthenticationResponse getAuthenticationResponse(LoginRequestDto requestDto, RequestMetadata requestMetadata) {
        Optional<UserIdentifier> optionalUserIdentifier = userIdentifierRepository.findByIdentifier(requestDto.getIdentifier());
        if (!optionalUserIdentifier.isPresent()) {
            return createUnknownAccountResponse(requestDto, requestMetadata);
        }
        UserIdentifier userIdentifier = optionalUserIdentifier.get();
        if (!passwordService.isSameHash(requestDto.getPassword(), userIdentifier.getUser().getPassword())) {
            return createInvalidCredentialResponse(userIdentifier, requestDto, requestMetadata);
        }
        return createSuccessfulAuthenticationResponse(userIdentifier, requestDto, requestMetadata);
    }

    private AuthenticationResponse createUnknownAccountResponse(LoginRequestDto requestDto, RequestMetadata requestMetadata) {
        AuthenticationResponse authenticationResponse = makeAuthenticationResponse(requestDto, requestMetadata, AuthenticationResponseType.UNKNOWN_ACCOUNT);
        return authenticationResponseRepository.save(authenticationResponse);
    }

    private AuthenticationResponse createInvalidCredentialResponse(UserIdentifier userIdentifier, LoginRequestDto requestDto, RequestMetadata requestMetadata) {
        AuthenticationResponse authenticationResponse = makeAuthenticationResponse(requestDto, requestMetadata, AuthenticationResponseType.INCORRECT_CREDENTIAL);
        authenticationResponse.setUserIdentifier(userIdentifier);
        return authenticationResponseRepository.save(authenticationResponse);
    }

    private AuthenticationResponse createSuccessfulAuthenticationResponse(UserIdentifier userIdentifier, LoginRequestDto requestDto, RequestMetadata requestMetadata) {
        AuthenticationResponse authenticationResponse = makeAuthenticationResponse(requestDto, requestMetadata, AuthenticationResponseType.SUCCESSFUL);
        authenticationResponse.setUserIdentifier(userIdentifier);
        return authenticationResponseRepository.save(authenticationResponse);
    }

    private AuthenticationResponse makeAuthenticationResponse(LoginRequestDto requestDto, RequestMetadata requestMetadata, AuthenticationResponseType authenticationResponseType) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAuthenticationResponseType(authenticationResponseType);
        authenticationResponse.setIdentifier(requestDto.getIdentifier());
        authenticationResponse.setIpAddress(requestMetadata.getIpAddress());
        authenticationResponse.setUserAgent(requestMetadata.getUserAgent());
        return authenticationResponse;
    }

}
