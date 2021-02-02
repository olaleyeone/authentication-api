package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.dto.LoginApiRequest;
import com.olaleyeone.auth.data.dto.PasswordLoginApiRequest;
import com.olaleyeone.auth.data.dto.TotpLoginApiRequest;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Named;
import javax.inject.Provider;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class LoginAuthenticationService {

    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;
    private final PortalUserAuthenticationDataService portalUserAuthenticationDataService;
    private final Provider<TaskContext> taskContextProvider;

    protected PortalUserAuthentication createFailureResponse(LoginApiRequest requestDto, PortalUserAuthentication userAuthentication) {
        return taskContextProvider.get().executeAndReturn("FAILED LOGIN",
                String.format("Login failed with %s for %s",
                        userAuthentication.getResponseType(),
                        requestDto.getIdentifier()),
                () -> portalUserAuthenticationRepository.save(userAuthentication));
    }

    protected PortalUserAuthentication createInvalidCredentialResponse(
            LoginApiRequest requestDto,
            PortalUserAuthentication userAuthentication) {
        PortalUserIdentifier userIdentifier = userAuthentication.getPortalUserIdentifier();
        return taskContextProvider.get().executeAndReturn("FAILED LOGIN",
                "Invalid credentials for account " + requestDto.getIdentifier(),
                () -> {
                    userAuthentication.setPortalUserIdentifier(userIdentifier);
                    return portalUserAuthenticationRepository.save(userAuthentication);
                });
    }

    protected PortalUserAuthentication createSuccessfulAuthenticationResponse(
            PortalUserAuthentication userAuthentication, LoginApiRequest requestDto) {
        PortalUserIdentifier userIdentifier = userAuthentication.getPortalUserIdentifier();
        return taskContextProvider.get().executeAndReturn("SUCCESSFUL LOGIN",
                requestDto.getIdentifier() + " logged in",
                () -> {
                    userAuthentication.setPortalUserIdentifier(userIdentifier);
                    if (BooleanUtils.isTrue(requestDto.getInvalidateOtherSessions())) {
                        portalUserAuthenticationRepository.deactivateOtherSessions(userIdentifier.getPortalUser());
                    }
                    PortalUserAuthentication portalUserAuthentication = portalUserAuthenticationRepository.save(userAuthentication);
                    if (requestDto.getData() != null) {
                        requestDto.getData().forEach(it -> portalUserAuthenticationDataService.addData(portalUserAuthentication, it));
                    }
                    return portalUserAuthentication;
                });
    }

    protected PortalUserAuthentication makeAuthenticationResponse(
            PasswordLoginApiRequest requestDto,
            RequestMetadata requestMetadata,
            AuthenticationResponseType authenticationResponseType,
            Optional<PortalUserIdentifier> portalUserIdentifier) {

        PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
        userAuthentication.setType(AuthenticationType.LOGIN);
        userAuthentication.setResponseType(authenticationResponseType);
        userAuthentication.setIdentifier(requestDto.getIdentifier());
        userAuthentication.setIpAddress(requestMetadata.getIpAddress());
        userAuthentication.setUserAgent(requestMetadata.getUserAgent());
        userAuthentication.setFirebaseToken(requestDto.getFirebaseToken());
        userAuthentication.setRefreshTokenDurationInSeconds(requestDto.getRefreshTokenDurationInSeconds());
        portalUserIdentifier.ifPresent(userAuthentication::setPortalUserIdentifier);
        return userAuthentication;
    }

    protected PortalUserAuthentication makeAuthenticationResponse(
            TotpLoginApiRequest requestDto,
            RequestMetadata requestMetadata,
            AuthenticationResponseType authenticationResponseType,
            Optional<PortalUserIdentifier> portalUserIdentifier) {

        PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
        userAuthentication.setType(AuthenticationType.TOTP_LOGIN);
        userAuthentication.setResponseType(authenticationResponseType);
        userAuthentication.setIdentifier(requestDto.getIdentifier());
        userAuthentication.setIpAddress(requestMetadata.getIpAddress());
        userAuthentication.setUserAgent(requestMetadata.getUserAgent());
        userAuthentication.setFirebaseToken(requestDto.getFirebaseToken());
        userAuthentication.setRefreshTokenDurationInSeconds(requestDto.getRefreshTokenDurationInSeconds());
        portalUserIdentifier.ifPresent(userAuthentication::setPortalUserIdentifier);
        return userAuthentication;
    }

}
