package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.User;
import com.olaleyeone.auth.data.entity.UserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.LoginRequestDto;
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
                    authenticationResponse.setAuthenticationResponseType(AuthenticationResponseType.INCORRECT_CREDENTIAL);
                    return authenticationResponse;
                });
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithCorrectCredentials() throws Exception {
        User user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());

        UserIdentifier userIdentifier = new UserIdentifier();
        userIdentifier.setUser(user);

        Mockito.when(authenticationService.getAuthenticationResponse(Mockito.any(), Mockito.any()))
                .then(invocation -> {
                    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                    authenticationResponse.setAuthenticationResponseType(AuthenticationResponseType.SUCCESSFUL);
                    authenticationResponse.setUserIdentifier(userIdentifier);
                    return authenticationResponse;
                });
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UserPojo userPojo = objectMapper.readValue(result.getResponse().getContentAsString(), UserPojo.class);
                    assertNotNull(userPojo);
                    assertEquals(user.getFirstName(), userPojo.getFirstName());
                    assertEquals(user.getLastName(), userPojo.getLastName());
                });
    }
}