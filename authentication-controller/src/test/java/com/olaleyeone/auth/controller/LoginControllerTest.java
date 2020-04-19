package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.data.LoginRequestDto;
import com.olaleyeone.auth.response.handler.UserPojoHandler;
import com.olaleyeone.auth.response.pojo.UserPojo;
import com.olaleyeone.auth.service.AuthenticationService;
import com.olaleyeone.auth.test.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
class LoginControllerTest extends ControllerTest {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserPojoHandler userPojoHandler;

    private LoginRequestDto loginRequestDto;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setIdentifier(faker.internet().emailAddress());
        loginRequestDto.setPassword(faker.internet().password());
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
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithCorrectCredentials() throws Exception {

        UserPojo userPojo = new UserPojo();

        Mockito.when(authenticationService.getAuthenticationResponse(Mockito.any(), Mockito.any()))
                .then(invocation -> {
                    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                    authenticationResponse.setResponseType(AuthenticationResponseType.SUCCESSFUL);
                    return authenticationResponse;
                });
        Mockito.when(userPojoHandler.getUserPojo(Mockito.any()))
                .then(invocation -> userPojo);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UserPojo response = objectMapper.readValue(result.getResponse().getContentAsString(), UserPojo.class);
                    assertNotNull(response);
                    assertEquals(userPojo, response);
                });
    }
}