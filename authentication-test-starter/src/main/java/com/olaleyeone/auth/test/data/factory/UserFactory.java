package com.olaleyeone.auth.test.data.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.User;

public class UserFactory implements FactoryHelper<User> {

    @Override
    public Class<User> getEntity() {
        return User.class;
    }

    @Override
    public User apply(Faker faker, ModelFactory factory) {
        User user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(faker.internet().password());
        return user;
    }
}
