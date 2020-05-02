package com.olaleyeone.auth.controllertest;

import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.repository.SignatureKeyRepository;
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
}
