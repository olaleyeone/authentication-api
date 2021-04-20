package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.auth.data.dto.TotpLoginApiRequest;
import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.OneTimePasswordRepository;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class TotpLoginAuthenticationServiceImpl implements TotpLoginAuthenticationService {

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final HashService hashService;
    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final LoginAuthenticationService loginAuthenticationService;
    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;

    @Activity("PROCESS USER LOGIN")
    @Transactional
    @Override
    public PortalUserAuthentication getAuthenticationResponse(TotpLoginApiRequest apiRequest, RequestMetadata requestMetadata) {
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

        Optional<OneTimePassword> optionalOneTimePassword = oneTimePasswordRepository.getActive(userIdentifier, apiRequest.getPassword());
        if (!optionalOneTimePassword.isPresent()) {
            PortalUserAuthentication userAuthentication = loginAuthenticationService.makeAuthenticationResponse(
                    apiRequest,
                    requestMetadata,
                    AuthenticationResponseType.UNKNOWN_OTP,
                    optionalUserIdentifier);
            return loginAuthenticationService.createFailureResponse(apiRequest, userAuthentication);
        }

        OneTimePassword oneTimePassword = optionalOneTimePassword.get();

//        if (!oneTimePassword.getUserIdentifier().getId().equals(userIdentifier.getId())) {
//            PortalUserAuthentication userAuthentication = loginAuthenticationService.makeAuthenticationResponse(
//                    apiRequest,
//                    requestMetadata,
//                    AuthenticationResponseType.INCORRECT_IDENTIFIER,
//                    optionalUserIdentifier);
//            userAuthentication.setOneTimePassword(oneTimePassword);
//            return loginAuthenticationService.createFailureResponse(apiRequest, userAuthentication);
//        }

        if (!hashService.isSameHash(apiRequest.getPassword(), oneTimePassword.getHash())) {
            PortalUserAuthentication userAuthentication = loginAuthenticationService.makeAuthenticationResponse(
                    apiRequest,
                    requestMetadata,
                    AuthenticationResponseType.INCORRECT_CREDENTIAL,
                    optionalUserIdentifier);
            userAuthentication.setOneTimePassword(oneTimePassword);
            return loginAuthenticationService.createInvalidCredentialResponse(apiRequest, userAuthentication);
        }

        if (OffsetDateTime.now().isAfter(oneTimePassword.getExpiresAt())) {
            PortalUserAuthentication userAuthentication = loginAuthenticationService.makeAuthenticationResponse(
                    apiRequest,
                    requestMetadata,
                    AuthenticationResponseType.EXPIRED_OTP,
                    optionalUserIdentifier);
            userAuthentication.setOneTimePassword(oneTimePassword);
            return loginAuthenticationService.createFailureResponse(apiRequest, userAuthentication);
        }

        if (portalUserAuthenticationRepository.countByOneTimePassword(oneTimePassword) > 0) {
            PortalUserAuthentication userAuthentication = loginAuthenticationService.makeAuthenticationResponse(
                    apiRequest,
                    requestMetadata,
                    AuthenticationResponseType.OTP_ALREADY_USED,
                    optionalUserIdentifier);
            userAuthentication.setOneTimePassword(oneTimePassword);
            return loginAuthenticationService.createInvalidCredentialResponse(apiRequest, userAuthentication);
        }

        PortalUserAuthentication userAuthentication = loginAuthenticationService.makeAuthenticationResponse(
                apiRequest,
                requestMetadata,
                AuthenticationResponseType.SUCCESSFUL,
                optionalUserIdentifier);
        userAuthentication.setOneTimePassword(oneTimePassword);
        return loginAuthenticationService.createSuccessfulAuthenticationResponse(userAuthentication, apiRequest);
    }

}
