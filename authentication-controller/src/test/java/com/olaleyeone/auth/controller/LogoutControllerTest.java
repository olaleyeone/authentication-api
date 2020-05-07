package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.security.access.AccessStatus;
import com.olaleyeone.auth.security.authorizer.NotClientTokenAuthorizer;
import com.olaleyeone.auth.service.LogoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogoutControllerTest extends ControllerTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private NotClientTokenAuthorizer authorizer;

    @Autowired
    private LogoutService logoutService;

    @BeforeEach
    void setUp() {
        Mockito.doReturn(AccessStatus.allowed()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        Mockito.doReturn(String.valueOf(faker.number().randomNumber())).when(accessClaims)
                .getSubject();
        Mockito.doReturn(String.valueOf(faker.number().randomNumber())).when(accessClaims)
                .getId();
    }

    @Test
    void logoutForActiveUser() throws Exception {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(new PortalUserAuthentication());
        Mockito.doReturn(Optional.of(refreshToken)).when(refreshTokenRepository).findActiveToken(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                .with(loggedInUser))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Cookie refreshTokenCookie = result.getResponse().getCookie(UserApiResponseHandler.REFRESH_TOKEN_COOKIE_NAME);
                    assertNotNull(refreshTokenCookie);
                    assertEquals(0, refreshTokenCookie.getMaxAge());

                    Cookie accessTokenCookie = result.getResponse().getCookie(UserApiResponseHandler.ACCESS_TOKEN_COOKIE_NAME);
                    assertNotNull(accessTokenCookie);
                    assertEquals(0, accessTokenCookie.getMaxAge());
                });
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findActiveToken(Long.valueOf(accessClaims.getId()));
        Mockito.verify(logoutService, Mockito.times(1))
                .logout(refreshToken.getActualAuthentication());
    }

    @Test
    void logoutForInactiveUser() throws Exception {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(new PortalUserAuthentication());
        Mockito.doReturn(Optional.empty()).when(refreshTokenRepository).findActiveToken(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                .with(loggedInUser))
                .andExpect(status().isOk());
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findActiveToken(Long.valueOf(accessClaims.getId()));
        Mockito.verify(logoutService, Mockito.never())
                .logout(Mockito.any());
    }
}