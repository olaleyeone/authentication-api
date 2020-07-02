package com.olaleyeone.auth.entitytest.data.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;

import java.time.OffsetDateTime;

public class RefreshTokenFactory implements FactoryHelper<RefreshToken> {

    @Override
    public Class<RefreshToken> getEntity() {
        return RefreshToken.class;
    }

    @Override
    public RefreshToken apply(Faker faker, ModelFactory factory) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(factory.create(PortalUserAuthentication.class));
        refreshToken.setExpiresAt(OffsetDateTime.now().plusMinutes(10));
        refreshToken.setAccessExpiresAt(OffsetDateTime.now().plusMinutes(1));
        return refreshToken;
    }
}
