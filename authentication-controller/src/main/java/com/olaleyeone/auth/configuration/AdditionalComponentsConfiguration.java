package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.dto.data.RequestMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.inject.Provider;

@Configuration
public class AdditionalComponentsConfiguration {

    @Bean
    @Scope(ConfigurableWebApplicationContext.SCOPE_REQUEST)
    public RequestMetadata requestMetadata(Provider<com.olaleyeone.auth.security.data.RequestMetadata> requestMetadataProvider) {
        com.olaleyeone.auth.security.data.RequestMetadata metadata = requestMetadataProvider.get();

        RequestMetadata requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(metadata.getIpAddress());
        requestMetadata.setUserAgent(metadata.getUserAgent());
        if (metadata.getAccessClaims() != null) {
            requestMetadata.setRefreshTokenId(Long.valueOf(metadata.getAccessClaims().getId()));
        }
        return requestMetadata;
    }
}
