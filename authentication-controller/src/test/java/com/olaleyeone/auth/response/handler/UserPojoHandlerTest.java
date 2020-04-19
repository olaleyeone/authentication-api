package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.response.pojo.UserPojo;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.RefreshTokenService;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserPojoHandlerTest extends ComponentTest {

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtService jwtService;

    private UserPojoHandler handler;

    private PortalUser user;
    private PortalUserIdentifier userIdentifier;
    private RefreshToken refreshToken;
    private AuthenticationResponse authenticationResponse;

    @BeforeEach
    void setUp() {

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

        handler = new UserPojoHandler(refreshTokenService, jwtService);
    }

    @Test
    public void getUserPojoForAuthentication() {
        String refreshJws = UUID.randomUUID().toString();
        String accessJws = UUID.randomUUID().toString();

        Mockito.when(refreshTokenService.createRefreshToken(Mockito.any()))
                .then(invocation -> refreshToken);
        Mockito.when(jwtService.getRefreshToken(Mockito.any()))
                .then(invocation -> refreshJws);
        Mockito.when(jwtService.getAccessToken(Mockito.any()))
                .then(invocation -> accessJws);

        UserPojo userPojo = handler.getUserPojo(authenticationResponse);
        assertEquals(user.getFirstName(), userPojo.getFirstName());
        assertEquals(user.getLastName(), userPojo.getLastName());
        assertEquals(refreshJws, userPojo.getRefreshToken());
        assertEquals(accessJws, userPojo.getAccessToken());
    }
}