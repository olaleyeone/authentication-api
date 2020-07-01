package com.olaleyeone.auth.test;


import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.entitytest.data.EntityFactoryConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

@ExtendWith(MockitoExtension.class)
public class ComponentTest {

    protected final Faker faker = Faker.instance(new Random());
    protected final ModelFactory modelFactory = new EntityFactoryConfiguration().entityFactory(faker, null);
}
