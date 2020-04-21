package com.olaleyeone.auth.entitytest.data.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUser;

public class PortalUserFactory implements FactoryHelper<PortalUser> {

    @Override
    public Class<PortalUser> getEntity() {
        return PortalUser.class;
    }

    @Override
    public PortalUser apply(Faker faker, ModelFactory factory) {
        PortalUser user = new PortalUser();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(faker.internet().password());
        return user;
    }
}
