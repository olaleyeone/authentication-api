package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.data.LoginApiRequest;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.LoginAuthenticationService;
import com.olaleyeone.auth.controllertest.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest extends ControllerTest {

    @Autowired
    private LoginAuthenticationService authenticationService;

    @Autowired
    private AccessTokenApiResponseHandler accessTokenApiResponseHandler;

    private LoginApiRequest loginApiRequest;

    @BeforeEach
    void setUp() {
        loginApiRequest = new LoginApiRequest();
        loginApiRequest.setIdentifier(faker.internet().emailAddress());
        loginApiRequest.setPassword(faker.internet().password());
    }

    @Test
    void loginWithIncorrectCredentials() throws Exception {
        Mockito.when(authenticationService.getAuthenticationResponse(Mockito.any(), Mockito.any()))
                .then(invocation -> {
                    PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
                    userAuthentication.setResponseType(AuthenticationResponseType.INCORRECT_CREDENTIAL);
                    return userAuthentication;
                });
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .with(body(loginApiRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithCorrectCredentials() throws Exception {

        AccessTokenApiResponse accessTokenApiResponse = new AccessTokenApiResponse();

        Mockito.when(authenticationService.getAuthenticationResponse(Mockito.any(), Mockito.any()))
                .then(invocation -> {
                    PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
                    userAuthentication.setResponseType(AuthenticationResponseType.SUCCESSFUL);
                    return userAuthentication;
                });
        Mockito.when(accessTokenApiResponseHandler.getAccessToken(Mockito.any(PortalUserAuthentication.class)))
                .then(invocation -> ResponseEntity.ok(accessTokenApiResponse));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .with(body(loginApiRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    AccessTokenApiResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AccessTokenApiResponse.class);
                    assertNotNull(response);
                    assertEquals(accessTokenApiResponse, response);
                });
    }
}