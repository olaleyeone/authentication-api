package com.olaleyeone.auth.controllertest;

import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ResponseHandlerMockConfig {

    @Bean
    public UserApiResponseHandler userApiResponseHandler() {
        return Mockito.mock(UserApiResponseHandler.class);
    }
}
