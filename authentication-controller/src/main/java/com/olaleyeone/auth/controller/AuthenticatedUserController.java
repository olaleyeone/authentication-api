package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.security.data.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;

@RequiredArgsConstructor
@RestController
public class AuthenticatedUserController {

    private final UserApiResponseHandler userApiResponseHandler;

    @Autowired
    private Provider<RequestMetadata> requestMetadataProvider;

    @GetMapping("/me")
    public UserApiResponse getUserDetails() {
        return userApiResponseHandler.getUserApiResponse(Long.valueOf(requestMetadataProvider.get().getTokenClaims().getSubject()));
    }
}
