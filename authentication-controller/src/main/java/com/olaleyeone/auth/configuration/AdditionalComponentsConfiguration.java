package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.dto.data.RequestMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class AdditionalComponentsConfiguration {

    @Bean
    @Scope(ConfigurableWebApplicationContext.SCOPE_REQUEST)
    public RequestMetadata requestMetadata(HttpServletRequest httpServletRequest) {
        RequestMetadata requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(httpServletRequest.getRemoteAddr());
        requestMetadata.setUserAgent(httpServletRequest.getHeader(HttpHeaders.USER_AGENT));
        return requestMetadata;
    }
}
