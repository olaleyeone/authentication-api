package com.olaleyeone.auth.controllertest;

import com.olaleyeone.auth.integration.auth.JwtService;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import com.olaleyeone.auth.security.data.AccessClaimsExtractor;
import com.olaleyeone.auth.security.access.TrustedIpAddressAuthorizer;
import com.olaleyeone.auth.security.authorizer.NotClientTokenAuthorizer;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SecurityMockConfig {

    @Bean
    public TrustedIpAddressAuthorizer trustedIpAddressAuthorizer() {
        return Mockito.mock(TrustedIpAddressAuthorizer.class);
    }

    @Bean
    public AccessClaimsExtractor accessClaimsExtractor() {
        return Mockito.mock(AccessClaimsExtractor.class);
    }

    @Bean
    public NotClientTokenAuthorizer notClientTokenAccessManager() {
        return Mockito.mock(NotClientTokenAuthorizer.class);
    }

    @JwtToken(JwtTokenType.REFRESH)
    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }
}
