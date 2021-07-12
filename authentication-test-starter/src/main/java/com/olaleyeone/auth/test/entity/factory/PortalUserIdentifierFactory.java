package com.olaleyeone.auth.test.entity.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.UserIdentifierType;

public class PortalUserIdentifierFactory implements FactoryHelper<PortalUserIdentifier> {

    @Override
    public Class<PortalUserIdentifier> getEntity() {
        return PortalUserIdentifier.class;
    }

    @Override
    public PortalUserIdentifier apply(Faker faker, ModelFactory factory) {
        PortalUserIdentifier userIdentifier = new PortalUserIdentifier();
        userIdentifier.setPortalUser(factory.create(PortalUser.class));
        userIdentifier.setIdentifier(faker.internet().emailAddress());
        userIdentifier.setIdentifierType(UserIdentifierType.EMAIL_ADDRESS);
        return userIdentifier;
    }
}
