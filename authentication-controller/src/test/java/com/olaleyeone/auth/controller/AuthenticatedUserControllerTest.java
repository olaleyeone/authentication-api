package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.security.access.AccessTokenValidator;
import com.olaleyeone.auth.test.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticatedUserControllerTest extends ControllerTest {

    @Autowired
    private AccessTokenValidator accessTokenValidator;

    @Autowired
    private UserApiResponseHandler userApiResponseHandler;

    private String token;
    private String userId;

    @BeforeEach
    public void setUp() {
        token = UUID.randomUUID().toString();
        userId = String.valueOf(faker.number().randomNumber());
        Mockito.when(accessTokenValidator.resolveToUserId(Mockito.any()))
                .then(invocation -> userId);
    }

    @Test
    void getUserDetails() throws Exception {
        UserApiResponse userApiResponse = new UserApiResponse();
        userApiResponse.setId(faker.number().randomNumber());
        Mockito.doReturn(userApiResponse).when(userApiResponseHandler).getUserApiResponse(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.get("/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UserApiResponse response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), UserApiResponse.class);
                    assertNotNull(response);
                    assertEquals(userApiResponse.getId(), response.getId());
                });
        Mockito.verify(userApiResponseHandler, Mockito.times(1))
                .getUserApiResponse(Long.valueOf(userId));
    }

    @Test
    void shouldValidateToke() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
        Mockito.verify(accessTokenValidator, Mockito.times(1))
                .resolveToUserId(token);
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        Mockito.doReturn(null).when(accessTokenValidator).resolveToUserId(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.get("/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
        Mockito.verify(accessTokenValidator, Mockito.times(1))
                .resolveToUserId(token);
    }
}