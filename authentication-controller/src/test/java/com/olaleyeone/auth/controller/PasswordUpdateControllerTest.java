package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.access.AccessStatus;
import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.PasswordUpdateApiRequest;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.security.authorizer.NotClientTokenAuthorizer;
import com.olaleyeone.auth.service.PasswordUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PasswordUpdateControllerTest extends ControllerTest {

    @Autowired
    private NotClientTokenAuthorizer authorizer;

    @Autowired
    private PasswordUpdateService passwordUpdateService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshToken refreshToken;

    @BeforeEach
    public void setUp() {
        refreshToken = new RefreshToken();
    }

    @Test
    void changePassword() throws Exception {
        Mockito.doReturn(AccessStatus.allowed()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        Mockito.doReturn(faker.number().digit()).when(accessClaims).getId();
        Mockito.doReturn(Optional.of(refreshToken)).when(refreshTokenRepository).findById(Mockito.any());

        PasswordUpdateApiRequest apiRequest = new PasswordUpdateApiRequest();
        apiRequest.setPassword(faker.internet().password());
        apiRequest.setInvalidateOtherSessions(faker.bool().bool());
        mockMvc.perform(MockMvcRequestBuilders.post("/password")
                .with(loggedInUser)
                .with(body(apiRequest)))
                .andExpect(status().isOk());
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findById(Long.valueOf(accessClaims.getId()));
        Mockito.verify(passwordUpdateService, Mockito.times(1))
                .updatePassword(Mockito.same(refreshToken), Mockito.argThat(argument -> {
                    assertEquals(apiRequest.getPassword(), argument.getPassword());
                    assertEquals(apiRequest.getInvalidateOtherSessions(), argument.getInvalidateOtherSessions());
                    return true;
                }));
    }

    @Test
    void shouldRequireNonClientToken() throws Exception {
        Mockito.doReturn(AccessStatus.denied()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/password").with(loggedInUser))
                .andExpect(status().isForbidden());
    }
}