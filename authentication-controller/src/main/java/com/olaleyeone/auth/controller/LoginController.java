package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.data.LoginApiRequest;
import com.olaleyeone.auth.dto.data.RequestMetadata;
import com.olaleyeone.auth.exception.ErrorResponse;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final AuthenticationService authenticationService;
    private final Provider<RequestMetadata> requestMetadata;
    private final UserApiResponseHandler userApiResponseHandler;

    @PostMapping("/login")
    public UserApiResponse login(@Valid @RequestBody LoginApiRequest dto) {
        AuthenticationResponse authenticationResponse = authenticationService.getAuthenticationResponse(dto, requestMetadata.get());
        if (authenticationResponse.getResponseType() != AuthenticationResponseType.SUCCESSFUL) {
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED);
        }
        return userApiResponseHandler.getUserApiResponse(authenticationResponse);
    }
}
