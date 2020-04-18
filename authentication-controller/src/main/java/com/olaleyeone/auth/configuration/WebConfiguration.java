package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.dto.RequestMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Configuration
@ComponentScan({
        "com.olaleyeone.auth.controller",
        "com.olaleyeone.auth.advice"
})
public class WebConfiguration {

    @Bean
    @Scope(ConfigurableWebApplicationContext.SCOPE_REQUEST)
    public RequestMetadata requestMetadata(HttpServletRequest httpServletRequest) {
        RequestMetadata requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(httpServletRequest.getRemoteAddr());
        requestMetadata.setUserAgent(httpServletRequest.getHeader(HttpHeaders.USER_AGENT));
        return requestMetadata;
    }
}
