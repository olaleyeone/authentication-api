package com.olaleyeone.auth.test;


import com.github.javafaker.Faker;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Random;

@ExtendWith(MockitoExtension.class)
public abstract class ComponentTest {

    protected Faker faker = Faker.instance(new Random());
}
