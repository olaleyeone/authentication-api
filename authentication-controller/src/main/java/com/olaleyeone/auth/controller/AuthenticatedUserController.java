package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.security.constraint.NotClientToken;
import com.olaleyeone.data.RequestMetadata;
import com.olaleyeone.web.exception.ErrorResponse;
import com.olaleyeone.web.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;

@RequiredArgsConstructor
@RestController
public class AuthenticatedUserController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserApiResponseHandler accessTokenApiResponseHandler;

    @Autowired
    private Provider<RequestMetadata> requestMetadataProvider;

    @NotClientToken
    @GetMapping("/me")
    public HttpEntity<UserApiResponse> getUserDetails() {
        RefreshToken refreshToken = refreshTokenRepository.findActiveToken(Long.valueOf(requestMetadataProvider.get().getRefreshTokenId()))
                .orElseThrow(() -> new ErrorResponse(HttpStatus.UNAUTHORIZED));
        return accessTokenApiResponseHandler.getAccessToken(refreshToken);
    }
}
