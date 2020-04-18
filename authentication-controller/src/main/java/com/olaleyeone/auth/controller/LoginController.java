package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.LoginRequestDto;
import com.olaleyeone.auth.dto.RequestMetadata;
import com.olaleyeone.auth.exception.ErrorResponse;
import com.olaleyeone.auth.response.pojo.UserPojo;
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

    @PostMapping("/login")
    public UserPojo login(@Valid @RequestBody LoginRequestDto dto) {
        AuthenticationResponse authenticationResponse = authenticationService.getAuthenticationResponse(dto, requestMetadata.get());
        if (authenticationResponse.getResponseType() == AuthenticationResponseType.SUCCESSFUL) {
            return new UserPojo(authenticationResponse.getPortalUserIdentifier().getPortalUser());
        }
        throw new ErrorResponse(HttpStatus.UNAUTHORIZED);
    }
}
