package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.data.LoginApiRequest;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.service.AuthenticationService;
import com.olaleyeone.auth.test.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest extends ControllerTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserApiResponseHandler userApiResponseHandler;

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
                    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                    authenticationResponse.setResponseType(AuthenticationResponseType.INCORRECT_CREDENTIAL);
                    return authenticationResponse;
                });
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginApiRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithCorrectCredentials() throws Exception {

        UserApiResponse userApiResponse = new UserApiResponse();

        Mockito.when(authenticationService.getAuthenticationResponse(Mockito.any(), Mockito.any()))
                .then(invocation -> {
                    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                    authenticationResponse.setResponseType(AuthenticationResponseType.SUCCESSFUL);
                    return authenticationResponse;
                });
        Mockito.when(userApiResponseHandler.getUserApiResponse(Mockito.any(AuthenticationResponse.class)))
                .then(invocation -> userApiResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginApiRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UserApiResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserApiResponse.class);
                    assertNotNull(response);
                    assertEquals(userApiResponse, response);
                });
    }
}