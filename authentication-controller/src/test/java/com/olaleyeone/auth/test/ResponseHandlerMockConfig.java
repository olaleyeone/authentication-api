package com.olaleyeone.auth.test;

import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ResponseHandlerMockConfig {

    @Bean
    public AccessTokenApiResponseHandler accessTokenApiResponseHandler() {
        return Mockito.mock(AccessTokenApiResponseHandler.class);
    }

    @Bean
    public UserApiResponseHandler userApiResponseHandler() {
        return Mockito.mock(UserApiResponseHandler.class);
    }
}
