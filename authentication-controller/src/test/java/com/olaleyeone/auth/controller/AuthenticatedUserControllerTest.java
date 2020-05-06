package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticatedUserControllerTest extends ControllerTest {

    @Autowired
    private AccessTokenApiResponseHandler userApiResponseHandler;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private String token;

    @BeforeEach
    public void setUp() {
        token = UUID.randomUUID().toString();
        Mockito.doReturn(String.valueOf(faker.number().randomNumber())).when(accessClaims)
                .getSubject();
        Mockito.doReturn(String.valueOf(faker.number().randomNumber())).when(accessClaims)
                .getId();
    }

    @Test
    void getUserDetails() throws Exception {
        UserApiResponse userApiResponse = new UserApiResponse();
        userApiResponse.setId(faker.number().randomNumber());

        RefreshToken refreshToken = new RefreshToken();
        Mockito.doReturn(Optional.of(refreshToken)).when(refreshTokenRepository).findById(Mockito.any());
        Mockito.doReturn(new HttpEntity<>(userApiResponse)).when(userApiResponseHandler).getAccessToken(Mockito.any(RefreshToken.class));

        mockMvc.perform(MockMvcRequestBuilders.get("/me")
                .with(loggedInUser))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UserApiResponse response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), UserApiResponse.class);
                    assertNotNull(response);
                    assertEquals(userApiResponse.getId(), response.getId());
                });
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findById(Long.valueOf(accessClaims.getId()));
        Mockito.verify(userApiResponseHandler, Mockito.times(1))
                .getAccessToken(refreshToken);
    }

    @Test
    void shouldValidateToken() throws Exception {
        RefreshToken refreshToken = new RefreshToken();
        Mockito.doReturn(Optional.of(refreshToken)).when(refreshTokenRepository).findById(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.get("/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findById(Long.valueOf(accessClaims.getId()));
        Mockito.verify(accessClaimsExtractor, Mockito.times(1))
                .getClaims(token);
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        Mockito.doReturn(null).when(accessClaimsExtractor).getClaims(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.get("/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
        Mockito.verify(accessClaimsExtractor, Mockito.times(1))
                .getClaims(token);
    }
}