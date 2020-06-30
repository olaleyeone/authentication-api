package com.olaleyeone.auth.controllertest;

import com.olaleyeone.auth.repository.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RepositoryMockConfig {

    @Bean
    public RefreshTokenRepository refreshTokenRepository() {
        return Mockito.mock(RefreshTokenRepository.class);
    }

    @Bean
    public SignatureKeyRepository signatureKeyRepository() {
        return Mockito.mock(SignatureKeyRepository.class);
    }

    @Bean
    public PortalUserIdentifierRepository portalUserIdentifierRepository() {
        return Mockito.mock(PortalUserIdentifierRepository.class);
    }

    @Bean
    public PortalUserRepository portalUserRepository() {
        return Mockito.mock(PortalUserRepository.class);
    }

    @Bean
    public PasswordResetRequestRepository passwordResetRequestRepository() {
        return Mockito.mock(PasswordResetRequestRepository.class);
    }
}
