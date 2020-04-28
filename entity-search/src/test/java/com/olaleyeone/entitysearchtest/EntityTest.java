package com.olaleyeone.entitysearchtest;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.internal.creation.bytebuddy.MockAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;

@SpringBootTest(classes = TestApplication.class)
public class EntityTest {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected EntityManager entityManager;

    @BeforeEach
    public void resetMocks() {
        applicationContext.getBeansOfType(MockAccess.class)
                .values().forEach(Mockito::reset);
    }
}
