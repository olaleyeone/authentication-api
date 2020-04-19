package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.data.LoginRequestDto;
import com.olaleyeone.auth.dto.data.RequestMetadata;
import com.olaleyeone.auth.exception.ErrorResponse;
import com.olaleyeone.auth.response.pojo.UserPojo;
import com.olaleyeone.auth.service.AuthenticationService;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public UserPojo login(@Valid @RequestBody LoginRequestDto dto) {
        AuthenticationResponse authenticationResponse = authenticationService.getAuthenticationResponse(dto, requestMetadata.get());
        if (authenticationResponse.getResponseType() != AuthenticationResponseType.SUCCESSFUL) {
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED);
        }
        PortalUser portalUser = authenticationResponse.getPortalUserIdentifier().getPortalUser();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticationResponse);

        UserPojo userPojo = new UserPojo(portalUser);
        userPojo.setRefreshToken(jwtService.getRefreshToken(refreshToken));
        userPojo.setAccessToken(jwtService.getAccessToken(portalUser));
        return userPojo;
    }
}
