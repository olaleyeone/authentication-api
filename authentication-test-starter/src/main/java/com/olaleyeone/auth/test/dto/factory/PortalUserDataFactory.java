package com.olaleyeone.auth.test.dto.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUserData;

public class PortalUserDataFactory implements FactoryHelper<PortalUserData> {

    @Override
    public Class<PortalUserData> getEntity() {
        return PortalUserData.class;
    }

    @Override
    public PortalUserData apply(Faker faker, ModelFactory factory) {
        PortalUserData portalUserData = new PortalUserData();
        portalUserData.setId(faker.number().randomNumber());
        portalUserData.setName(faker.name().firstName());
        portalUserData.setValue(faker.name().lastName());
        return portalUserData;
    }
}
