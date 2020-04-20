package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.AccessTokenDto;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
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

class AccessTokenApiResponseHandlerTest extends ComponentTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AccessTokenApiResponseHandler handler;

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
        userAuthentication.setPortalUser(userIdentifier.getPortalUser());

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
                .then(invocation -> AccessTokenDto.builder().token(accessJws).build());

        AccessTokenApiResponse accessTokenApiResponse = handler.getAccessToken(userAuthentication);
        assertEquals(user.getFirstName(), accessTokenApiResponse.getFirstName());
        assertEquals(user.getLastName(), accessTokenApiResponse.getLastName());
        assertEquals(refreshJws, accessTokenApiResponse.getRefreshToken());
        assertEquals(accessJws, accessTokenApiResponse.getAccessToken());
    }
}