package com.olaleyeone.auth.service;

import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;

import java.util.Random;

public class JwtServiceImplTestHelper {

    private JwtServiceImplTestHelper() {
        //noop
    }

    protected static Faker faker = Faker.instance(new Random());

    public static RefreshToken refreshToken() {
        PortalUser portalUser = new PortalUser();
        portalUser.setId(faker.number().randomNumber());

        PortalUserAuthentication userAuthentication = new PortalUserAuthentication();
        userAuthentication.setPortalUser(portalUser);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(faker.number().randomNumber());
        refreshToken.setActualAuthentication(userAuthentication);
        refreshToken.setPortalUser();
        return refreshToken;
    }
}
