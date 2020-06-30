package com.olaleyeone.auth.configuration;

import com.github.olaleyeone.auth.data.AuthorizedRequest;
import com.olaleyeone.data.dto.RequestMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Configuration
public class RequestMetadataConfiguration {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RequestMetadata requestMetadata(HttpServletRequest httpServletRequest, Provider<AuthorizedRequest> requestMetadataProvider) {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null;
        }

        AuthorizedRequest authorizedRequest = requestMetadataProvider.get();

        RequestMetadata requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(authorizedRequest.getIpAddress());
        requestMetadata.setUserAgent(authorizedRequest.getUserAgent());

        requestMetadata.setHost(httpServletRequest.getHeader(HttpHeaders.HOST));

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
