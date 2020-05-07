package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.integration.auth.JwtService;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.security.access.AccessStatus;
import com.olaleyeone.auth.security.access.TrustedIpAddressAuthorizer;
import com.olaleyeone.auth.security.data.AccessClaimsExtractor;
import com.olaleyeone.auth.security.data.AuthorizedRequestFactory;
import com.olaleyeone.auth.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

    @Bean
    public AuthorizedRequestFactory requestMetadataFactory(HttpServletRequest httpServletRequest, AccessClaimsExtractor accessClaimsExtractor) {
        return new AuthorizedRequestFactory(httpServletRequest, accessClaimsExtractor) {

            private String tokenPrefix = "Bearer ";

            @Override
            protected Optional<String> getAccessToken(HttpServletRequest httpServletRequest) {

                String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
                if (StringUtils.isNotBlank(authorizationHeader) && authorizationHeader.startsWith(tokenPrefix)) {
                    return Optional.of(authorizationHeader.substring(tokenPrefix.length()));
                }

                if (httpServletRequest.getCookies() == null) {
                    return Optional.empty();
                }

                return Arrays.asList(httpServletRequest.getCookies())
                        .stream()
                        .filter(cookie -> cookie.getName().equals(AccessTokenApiResponseHandler.ACCESS_TOKEN_COOKIE_NAME)
                                && cookie.isHttpOnly()
                                && cookie.getSecure())
                        .findFirst()
                        .map(Cookie::getValue);
            }
        };
    }

    @Profile("!test")
    @Bean
    public TrustedIpAddressAuthorizer trustedIpAddressAccessManager(SettingService settingService) {
        return (accessConstraint, ipAddress) -> {
            Optional<String> value = settingService.getString(StringUtils.defaultIfBlank(accessConstraint.value(), "TRUSTED_IP"));
            if (value.isPresent()) {
                return Arrays.asList(value.get().split(" *, *")).contains(ipAddress)
                        ? AccessStatus.allowed()
                        : AccessStatus.denied(ipAddress);
            }
            if (accessConstraint.defaultIpAddresses().length > 0) {
                return Arrays.asList(accessConstraint.defaultIpAddresses()).contains(ipAddress)
                        ? AccessStatus.allowed()
                        : AccessStatus.denied(ipAddress);
            }
            return AccessStatus.denied("");
        };
    }

    @Profile("!test")
    @Bean
    public AccessClaimsExtractor accessTokenValidator(@JwtToken(JwtTokenType.ACCESS) JwtService jwtService) {
        return token -> {
            try {
                return jwtService.parseToken(token);
            } catch (Exception e) {
                return null;
            }
        };
    }
}
