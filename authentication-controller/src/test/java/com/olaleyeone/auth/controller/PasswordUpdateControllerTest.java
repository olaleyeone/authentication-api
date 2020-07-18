package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.access.AccessStatus;
import com.olaleyeone.auth.constraints.HasPassword;
import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.dto.PasswordUpdateApiRequest;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.security.authorizer.NotClientTokenAuthorizer;
import com.olaleyeone.auth.service.PasswordUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PasswordUpdateControllerTest extends ControllerTest {

    @Autowired
    private NotClientTokenAuthorizer authorizer;

    @Autowired
    private PasswordUpdateService passwordUpdateService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private HashService hashService;

    @Autowired
    private HasPassword.Validator hasPasswordValidator;

    private RefreshToken refreshToken;

    @BeforeEach
    public void setUp() {
        PortalUser portalUser = new PortalUser();
        portalUser.setPassword(UUID.randomUUID().toString());

        PortalUserAuthentication portalUserAuthentication = new PortalUserAuthentication();
        portalUserAuthentication.setPortalUser(portalUser);
        refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(portalUserAuthentication);

        Mockito.doReturn(true).when(hasPasswordValidator).isValid(Mockito.any(), Mockito.any());
    }

    @Test
    void changePassword() throws Exception {
        Mockito.doReturn(AccessStatus.allowed()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        Mockito.doReturn(faker.number().digit()).when(accessClaims).getId();
        Mockito.doReturn(Optional.of(refreshToken)).when(refreshTokenRepository).findById(Mockito.any());


        PasswordUpdateApiRequest apiRequest = new PasswordUpdateApiRequest();
        apiRequest.setCurrentPassword(refreshToken.getPortalUser().getPassword());
        apiRequest.setPassword(faker.internet().password());
        apiRequest.setInvalidateOtherSessions(faker.bool().bool());

        Mockito.doReturn(true).when(hashService).isSameHash(Mockito.eq(apiRequest.getCurrentPassword()), Mockito.any());
        Mockito.doReturn(false).when(hashService).isSameHash(Mockito.eq(apiRequest.getPassword()), Mockito.any());

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
    void rejectPasswordReuse() throws Exception {
        Mockito.doReturn(AccessStatus.allowed()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        Mockito.doReturn(faker.number().digit()).when(accessClaims).getId();
        Mockito.doReturn(Optional.of(refreshToken)).when(refreshTokenRepository).findById(Mockito.any());

        PasswordUpdateApiRequest apiRequest = new PasswordUpdateApiRequest();
        String password = faker.internet().password();
        apiRequest.setCurrentPassword(password);
        apiRequest.setPassword(password);
        apiRequest.setInvalidateOtherSessions(faker.bool().bool());

        Mockito.doReturn(true).when(hashService).isSameHash(Mockito.eq(apiRequest.getCurrentPassword()), Mockito.any());
        Mockito.doReturn(true).when(hashService).isSameHash(Mockito.eq(apiRequest.getPassword()), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/password")
                .with(loggedInUser)
                .with(body(apiRequest)))
                .andExpect(status().isConflict());

        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findById(Long.valueOf(accessClaims.getId()));
        Mockito.verify(passwordUpdateService, Mockito.never())
                .updatePassword(Mockito.any(RefreshToken.class), Mockito.any());
    }

    @Test
    void rejectInvalidPassword() throws Exception {
        Mockito.doReturn(AccessStatus.allowed()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        Mockito.doReturn(faker.number().digit()).when(accessClaims).getId();
        Mockito.doReturn(Optional.of(refreshToken)).when(refreshTokenRepository).findById(Mockito.any());

        Mockito.doReturn(false).when(hashService).isSameHash(Mockito.any(), Mockito.any());

        PasswordUpdateApiRequest apiRequest = new PasswordUpdateApiRequest();
        apiRequest.setCurrentPassword(faker.internet().password());
        apiRequest.setPassword(faker.internet().password());
        apiRequest.setInvalidateOtherSessions(faker.bool().bool());

        mockMvc.perform(MockMvcRequestBuilders.post("/password")
                .with(loggedInUser)
                .with(body(apiRequest)))
                .andExpect(status().isForbidden());
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findById(Long.valueOf(accessClaims.getId()));
        Mockito.verify(passwordUpdateService, Mockito.never())
                .updatePassword(Mockito.any(RefreshToken.class), Mockito.any());
    }

    @Test
    void shouldRequireNonClientToken() throws Exception {
        Mockito.doReturn(AccessStatus.denied()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/password").with(loggedInUser))
                .andExpect(status().isForbidden());
    }
}