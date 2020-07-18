package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.integration.events.SessionUpdateEvent;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.security.constraint.NotClientToken;
import com.olaleyeone.auth.service.LogoutService;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class LogoutController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final LogoutService logoutService;
    private final ApplicationContext applicationContext;

    @Autowired
    private Provider<RequestMetadata> requestMetadataProvider;

    @NotClientToken
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        refreshTokenRepository.findActiveToken(Long.valueOf(requestMetadataProvider.get().getRefreshTokenId()))
                .ifPresent(refreshToken -> {
                    logoutService.logout(refreshToken.getActualAuthentication());
                    applicationContext.publishEvent(new SessionUpdateEvent(refreshToken.getActualAuthentication()));
                });

        clearCookie(response, AccessTokenApiResponseHandler.ACCESS_TOKEN_COOKIE_NAME);
        clearCookie(response, AccessTokenApiResponseHandler.REFRESH_TOKEN_COOKIE_NAME);
    }

    private void clearCookie(HttpServletResponse response, String accessTokenCookieName) {
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, "");
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);
    }
}
