package com.olaleyeone.auth.controllertest;

import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ResponseHandlerMockConfig {

    @Bean
    public AccessTokenApiResponseHandler userApiResponseHandler() {
        return Mockito.mock(AccessTokenApiResponseHandler.class);
    }
}
