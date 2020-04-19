package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.RefreshTokenService;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserApiResponseHandlerTest extends ComponentTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserApiResponseHandler handler;

    private PortalUser user;
    private PortalUserIdentifier userIdentifier;
    private RefreshToken refreshToken;
    private PortalUserAuthentication userAuthentication;

    @BeforeEach
    void setUp() {

        user = new PortalUser();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());

        userIdentifier = new PortalUserIdentifier();
        userIdentifier.setPortalUser(user);

        userAuthentication = new PortalUserAuthentication();
        userAuthentication.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        userAuthentication.setPortalUserIdentifier(userIdentifier);

        refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(userAuthentication);
    }

    @Test
    public void getUserPojoForAuthentication() {
        String refreshJws = UUID.randomUUID().toString();
        String accessJws = UUID.randomUUID().toString();

        Mockito.when(refreshTokenService.createRefreshToken(Mockito.any(PortalUserAuthentication.class)))
                .then(invocation -> refreshToken);
        Mockito.when(jwtService.getRefreshToken(Mockito.any()))
                .then(invocation -> refreshJws);
        Mockito.when(jwtService.getAccessToken(Mockito.any()))
                .then(invocation -> accessJws);

        UserApiResponse userApiResponse = handler.getUserApiResponse(userAuthentication);
        assertEquals(user.getFirstName(), userApiResponse.getFirstName());
        assertEquals(user.getLastName(), userApiResponse.getLastName());
        assertEquals(refreshJws, userApiResponse.getRefreshToken());
        assertEquals(accessJws, userApiResponse.getAccessToken());
    }
}