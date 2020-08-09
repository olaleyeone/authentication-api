package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.dto.TotpLoginApiRequest;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.TotpLoginAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TotpLoginControllerTest extends ControllerTest {

    @Autowired
    private TotpLoginAuthenticationService authenticationService;

    @Autowired
    private AccessTokenApiResponseHandler accessTokenApiResponseHandler;

    private TotpLoginApiRequest apiRequest;

    @BeforeEach
    void setUp() {
        apiRequest = new TotpLoginApiRequest();
        apiRequest.setTransactionId(faker.number().digit());
        apiRequest.setIdentifier(faker.internet().emailAddress());
        apiRequest.setPassword(faker.internet().password());
    }

    @Test
    void loginWithIncorrectCredentials() throws Exception {
        Mockito.when(authenticationService.getAuthenticationResponse(Mockito.any(TotpLoginApiRequest.class), Mockito.any()))
                .then(invocation -> {
                    PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
                    userAuthentication.setResponseType(AuthenticationResponseType.INCORRECT_CREDENTIAL);
                    return userAuthentication;
                });
        mockMvc.perform(MockMvcRequestBuilders.post("/totp/login")
                .with(body(apiRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithCorrectCredentials() throws Exception {

        AccessTokenApiResponse accessTokenApiResponse = new AccessTokenApiResponse();

        Mockito.when(authenticationService.getAuthenticationResponse(Mockito.any(TotpLoginApiRequest.class), Mockito.any()))
                .then(invocation -> {
                    PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
                    userAuthentication.setResponseType(AuthenticationResponseType.SUCCESSFUL);
                    return userAuthentication;
                });
        Mockito.when(accessTokenApiResponseHandler.getAccessToken(Mockito.any(PortalUserAuthentication.class)))
                .then(invocation -> ResponseEntity.ok(accessTokenApiResponse));

        mockMvc.perform(MockMvcRequestBuilders.post("/totp/login")
                .with(body(apiRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    AccessTokenApiResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AccessTokenApiResponse.class);
                    assertNotNull(response);
                    assertEquals(accessTokenApiResponse, response);
                });
    }

    @Test
    void loginToInactiveAccount() throws Exception {

        Mockito.when(authenticationService.getAuthenticationResponse(Mockito.any(TotpLoginApiRequest.class), Mockito.any()))
                .then(invocation -> {
                    PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
                    userAuthentication.setResponseType(AuthenticationResponseType.INACTIVE_ACCOUNT);
                    return userAuthentication;
                });

        mockMvc.perform(MockMvcRequestBuilders.post("/totp/login")
                .with(body(apiRequest)))
                .andExpect(status().isUnauthorized());
    }
}