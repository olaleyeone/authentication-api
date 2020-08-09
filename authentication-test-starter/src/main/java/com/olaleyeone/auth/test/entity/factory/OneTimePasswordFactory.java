package com.olaleyeone.auth.test.entity.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;

import java.time.OffsetDateTime;

public class OneTimePasswordFactory implements FactoryHelper<OneTimePassword> {

    @Override
    public Class<OneTimePassword> getEntity() {
        return OneTimePassword.class;
    }

    @Override
    public OneTimePassword apply(Faker faker, ModelFactory factory) {
        OneTimePassword oneTimePassword = new OneTimePassword();
        oneTimePassword.setUserIdentifier(factory.create(PortalUserIdentifier.class));
        oneTimePassword.setPassword(faker.internet().password());
        oneTimePassword.setHash(oneTimePassword.getPassword());
        oneTimePassword.setExpiresOn(OffsetDateTime.now().plusMinutes(1));
        return oneTimePassword;
    }
}
