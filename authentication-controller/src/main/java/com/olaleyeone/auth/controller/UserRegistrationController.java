package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.dto.data.RequestMetadata;
import com.olaleyeone.auth.dto.data.UserRegistrationApiRequest;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.security.annotations.Public;
import com.olaleyeone.auth.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;
    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;
    private final Provider<RequestMetadata> requestMetadata;

    @Public
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public AccessTokenApiResponse registerUser(@Valid @RequestBody UserRegistrationApiRequest dto) {
        PortalUserAuthentication portalUserAuthentication = userRegistrationService.registerUser(dto, requestMetadata.get());
        return accessTokenApiResponseHandler.getUserApiResponse(portalUserAuthentication);
    }
}
