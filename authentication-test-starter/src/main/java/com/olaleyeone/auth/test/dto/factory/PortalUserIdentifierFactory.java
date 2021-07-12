package com.olaleyeone.auth.test.dto.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
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
        userIdentifier.setId(faker.number().randomNumber());
        userIdentifier.setIdentifierType(UserIdentifierType.EMAIL_ADDRESS);
        userIdentifier.setIdentifier(faker.name().username());
        return userIdentifier;
    }
}
