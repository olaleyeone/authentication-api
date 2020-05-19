package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.data.AccessClaimsExtractor;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class AccessTokenController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;
    @JwtToken(JwtTokenType.REFRESH)
    private final AccessClaimsExtractor jwtService;

    @Public
    @PostMapping("/oauth2/token")
    public HttpEntity<AccessTokenApiResponse> getAccessToken(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() == null) {
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED);
        }

        Optional<String> optionalToken = Arrays.asList(httpServletRequest.getCookies())
                .stream()
                .filter(cookie -> cookie.getName().equals(AccessTokenApiResponseHandler.REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .map(Cookie::getValue);
        if (!optionalToken.isPresent()) {
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED);
        }

        try {
            AccessClaims accessClaims = jwtService.getClaims(optionalToken.get());

            RefreshToken refreshToken = refreshTokenRepository.findActiveToken(Long.valueOf(accessClaims.getId()))
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.UNAUTHORIZED));
            return accessTokenApiResponseHandler.getAccessToken(refreshToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED);
        }
    }
}
