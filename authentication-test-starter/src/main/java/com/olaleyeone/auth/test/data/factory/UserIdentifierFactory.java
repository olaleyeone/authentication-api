package com.olaleyeone.auth.test.data.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.User;
import com.olaleyeone.auth.data.entity.UserIdentifier;
import com.olaleyeone.auth.data.enums.UserIdentifierType;

public class UserIdentifierFactory implements FactoryHelper<UserIdentifier> {

    @Override
    public Class<UserIdentifier> getEntity() {
        return UserIdentifier.class;
    }

    @Override
    public UserIdentifier apply(Faker faker, ModelFactory factory) {
        UserIdentifier userIdentifier = new UserIdentifier();
        userIdentifier.setUser(factory.create(User.class));
        userIdentifier.setIdentifier(faker.internet().emailAddress());
        userIdentifier.setIdentifierType(UserIdentifierType.EMAIL);
        return userIdentifier;
    }
}
