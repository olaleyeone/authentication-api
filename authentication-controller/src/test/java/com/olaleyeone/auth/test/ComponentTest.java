package com.olaleyeone.auth.test;


import com.github.javafaker.Faker;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Random;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ComponentTest.$Config.class})
public abstract class ComponentTest {

    @Autowired
    protected Faker faker;

    @Configuration
    static class $Config {

        @Bean
        public Faker faker() {
            return Faker.instance(new Random());
        }
    }
}
