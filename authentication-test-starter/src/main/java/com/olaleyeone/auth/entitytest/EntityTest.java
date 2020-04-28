package com.olaleyeone.auth.entitytest;

import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.entitytest.data.TestDataFactoryConfiguration;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Map;

@Transactional
@SpringBootTest(classes = TestApplication.class)
@Import({TestDataFactoryConfiguration.class})
public class EntityTest {

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected ModelFactory modelFactory;

    @Autowired
    protected Faker faker;

    @Autowired
    protected ApplicationContext applicationContext;

    @AfterEach
    public void flushAfterEach() {
        if(!TestTransaction.isActive()){
            return;
        }
        SessionImplementor session = entityManager.unwrap(SessionImplementor.class);
        org.hibernate.engine.spi.PersistenceContext persistenceContext = session.getPersistenceContext();
        for (Map.Entry<Object, EntityEntry> entityEntry : persistenceContext.reentrantSafeEntityEntries()) {
            if (entityEntry.getValue().getStatus() == Status.SAVING) {
                return;
            }
        }
        entityManager.flush();
    }

    protected void saveAndFlush(Object... entities) {
        save(entities);
        entityManager.flush();
    }

    protected void save(Object[] entities) {
        for (Object entity : entities) {
            if (entityManager.contains(entity)) {
                entityManager.merge(entity);
            } else {
                entityManager.persist(entity);
            }
        }
    }
}
