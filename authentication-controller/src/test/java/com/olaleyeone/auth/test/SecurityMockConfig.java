package com.olaleyeone.auth.test;

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
}
