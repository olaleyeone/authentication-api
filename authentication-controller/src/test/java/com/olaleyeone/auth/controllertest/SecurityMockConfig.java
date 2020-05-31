package com.olaleyeone.auth.controllertest;

import com.github.olaleyeone.auth.access.TrustedIpAddressAuthorizer;
import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.data.AccessClaimsExtractor;
import com.github.olaleyeone.auth.data.AuthorizedRequest;
import com.olaleyeone.auth.integration.security.TokenGenerator;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.security.authorizer.NotClientTokenAuthorizer;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

@Configuration
class SecurityMockConfig {

    @Bean
    public TrustedIpAddressAuthorizer trustedIpAddressAuthorizer() {
        return Mockito.mock(TrustedIpAddressAuthorizer.class);
    }

    @Bean
    public NotClientTokenAuthorizer notClientTokenAccessManager() {
        return Mockito.mock(NotClientTokenAuthorizer.class);
    }

    @Bean
    public AuthorizedRequest authorizedRequest(HttpServletRequest request) {
        return Mockito.spy(new AuthorizedRequest() {

            @Override
            public String getIpAddress() {
                return request.getRemoteAddr();
            }

            @Override
            public String getUserAgent() {
                return request.getHeader(HttpHeaders.USER_AGENT);
            }

            @Override
            public String getAccessToken() {
                return request.getHeader(HttpHeaders.AUTHORIZATION);
            }

            @Override
            public AccessClaims getAccessClaims() {
                return accessClaims();
            }

            @Override
            public boolean isLocalhost() {
                return request.getRemoteAddr().equals(request.getLocalAddr());
            }
        });
    }

    @Bean
    public AccessClaims accessClaims() {
        return Mockito.mock(AccessClaims.class);
    }

    @JwtToken(JwtTokenType.REFRESH)
    @Bean
    public AccessClaimsExtractor accessClaimsExtractor() {
        return Mockito.mock(AccessClaimsExtractor.class);
    }

    @JwtToken(JwtTokenType.REFRESH)
    @Bean
    public TokenGenerator jwtService() {
        return Mockito.mock(TokenGenerator.class);
    }
}
