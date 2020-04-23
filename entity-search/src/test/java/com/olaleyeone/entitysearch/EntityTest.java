package com.olaleyeone.entitysearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

@SpringBootTest(classes = TestApplication.class)
public abstract class EntityTest {

    @Autowired
    protected EntityManager entityManager;
}
