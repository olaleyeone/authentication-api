package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.LoginRequestDto;
import com.olaleyeone.auth.response.pojo.UserPojo;
import com.olaleyeone.auth.service.AuthenticationService;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.RefreshTokenService;
import com.olaleyeone.auth.test.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
class LoginControllerTest extends ControllerTest {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtService jwtService;

    private LoginRequestDto loginRequestDto;
    private PortalUser user;
    private PortalUserIdentifier userIdentifier;
    private RefreshToken refreshToken;
    private AuthenticationResponse authenticationResponse;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setIdentifier(faker.internet().emailAddress());
        loginRequestDto.setPassword(faker.internet().password());

        user = new PortalUser();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());

        userIdentifier = new PortalUserIdentifier();
        userIdentifier.setPortalUser(user);

        authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        authenticationResponse.setPortalUserIdentifier(userIdentifier);

        refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(authenticationResponse);
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

        String refreshJws = UUID.randomUUID().toString();
        String accessJws = UUID.randomUUID().toString();

        Mockito.when(authenticationService.getAuthenticationResponse(Mockito.any(), Mockito.any()))
                .then(invocation -> authenticationResponse);
        Mockito.when(refreshTokenService.createRefreshToken(Mockito.any()))
                .then(invocation -> refreshToken);
        Mockito.when(jwtService.getRefreshToken(Mockito.any()))
                .then(invocation -> refreshJws);
        Mockito.when(jwtService.getAccessToken(Mockito.any()))
                .then(invocation -> accessJws);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UserPojo userPojo = objectMapper.readValue(result.getResponse().getContentAsString(), UserPojo.class);
                    assertNotNull(userPojo);
                    assertEquals(user.getFirstName(), userPojo.getFirstName());
                    assertEquals(user.getLastName(), userPojo.getLastName());
                    assertEquals(refreshJws, userPojo.getRefreshToken());
                    assertEquals(accessJws, userPojo.getAccessToken());
                });
    }
}