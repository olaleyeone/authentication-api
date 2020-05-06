package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.data.RequestMetadata;
import com.olaleyeone.web.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;

@RequiredArgsConstructor
@RestController
public class AuthenticatedUserController {

    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private Provider<RequestMetadata> requestMetadataProvider;

    @GetMapping("/me")
    public HttpEntity<UserApiResponse> getUserDetails() {
        RefreshToken refreshToken = refreshTokenRepository.findById(Long.valueOf(requestMetadataProvider.get().getRefreshTokenId()))
                .orElseThrow(NotFoundException::new);
        return accessTokenApiResponseHandler.getAccessToken(refreshToken);
    }
}
