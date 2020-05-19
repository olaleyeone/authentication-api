package com.olaleyeone.auth.integration.security;

import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;

import java.util.Random;

public final class JwtServiceImplTestHelper {

    protected static final Faker faker = Faker.instance(new Random());

    private JwtServiceImplTestHelper() {
        //noop
    }

    public static RefreshToken refreshToken() {
        PortalUser portalUser = new PortalUser();
        portalUser.setId(faker.number().randomNumber());

        PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
        userAuthentication.setPortalUser(portalUser);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(faker.number().randomNumber());
        refreshToken.setActualAuthentication(userAuthentication);
        return refreshToken;
    }
}
