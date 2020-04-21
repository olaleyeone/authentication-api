package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.dto.data.RequestMetadata;
import com.olaleyeone.auth.security.data.AuthorizedRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.inject.Provider;
import java.util.Optional;

@Configuration
public class AdditionalComponentsConfiguration {

    @Bean
    @Scope(ConfigurableWebApplicationContext.SCOPE_REQUEST)
    public RequestMetadata requestMetadata(Provider<AuthorizedRequest> requestMetadataProvider) {
        AuthorizedRequest authorizedRequest = requestMetadataProvider.get();

        RequestMetadata requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(authorizedRequest.getIpAddress());
        requestMetadata.setUserAgent(authorizedRequest.getUserAgent());
        if (authorizedRequest.getAccessClaims() != null) {
            Optional.ofNullable(authorizedRequest.getAccessClaims().getSubject())
                    .map(Long::valueOf)
                    .ifPresent(requestMetadata::setPortalUserId);
            Optional.ofNullable(authorizedRequest.getAccessClaims().getId())
                    .map(Long::valueOf)
                    .ifPresent(requestMetadata::setRefreshTokenId);
        }
        return requestMetadata;
    }
}
