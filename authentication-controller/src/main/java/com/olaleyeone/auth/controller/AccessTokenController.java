package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.data.AccessClaimsExtractor;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.dto.AccessTokenApiRequest;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class AccessTokenController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;
    @JwtToken(JwtTokenType.REFRESH)
    private final AccessClaimsExtractor accessClaimsExtractor;

    private final HttpServletRequest httpServletRequest;

    @Public
    @PostMapping("/oauth2/token")
    public HttpEntity<AccessTokenApiResponse> getAccessToken(@RequestBody Optional<AccessTokenApiRequest> accessTokenApiRequest) {

        String token = getToken(accessTokenApiRequest);

        if (StringUtils.isBlank(token)) {
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED);
        }

        try {
            AccessClaims accessClaims = accessClaimsExtractor.getClaims(token);
            if (accessClaims == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            RefreshToken refreshToken = refreshTokenRepository.findActiveToken(Long.valueOf(accessClaims.getId()))
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.UNAUTHORIZED));
            return accessTokenApiResponseHandler.getAccessToken(refreshToken);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String getToken(@RequestBody Optional<AccessTokenApiRequest> accessTokenApiRequest) {
        String token = accessTokenApiRequest.map(AccessTokenApiRequest::getRefreshToken).orElse(null);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        if (httpServletRequest.getCookies() == null) {
            return null;
        }
        return Arrays.asList(httpServletRequest.getCookies())
                .stream()
                .filter(cookie -> cookie.getName().equals(AccessTokenApiResponseHandler.REFRESH_TOKEN_COOKIE_NAME))
//                .peek(cookie -> logger.info("{}", cookie.getValue()))
                .findFirst()
                .map(Cookie::getValue).orElseThrow(() -> new ErrorResponse(HttpStatus.UNAUTHORIZED));
    }
}
