package com.olaleyeone.audittrail.impl;

import com.olalayeone.audittrailtest.EntityTest;
import com.olalayeone.audittrailtest.data.entity.Item;
import com.olalayeone.audittrailtest.data.entity.Student;
import com.olalayeone.audittrailtest.data.entity.User;
import com.olaleyeone.audittrail.api.AuditData;
import com.olaleyeone.audittrail.api.EntityIdentifier;
import com.olaleyeone.audittrail.entity.Audit;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EntityDataExtractorImplTest extends EntityTest {

    private EntityDataExtractorImpl entityDataExtractor;

    @BeforeEach
    void setUp() {
        entityDataExtractor = new EntityDataExtractorImpl(entityManager) {
            @Override
            public Class<?> getType(Object e) {

                if (e instanceof HibernateProxy) {
                    return ((HibernateProxy) e).getHibernateLazyInitializer().getPersistentClass();
                }
                return e.getClass();
            }
        };
    }

    @Test
    void getEntityData() {
        User user = new User();
        user.setId(faker.number().randomDigit());
        Student student = new Student(faker.number().randomDigit(), faker.name().fullName());
        student.setUser(user);
        Map<String, AuditData> entityData = entityDataExtractor.extractAttributes(student);
        assertTrue(entityData.containsKey("id"));
        assertTrue(entityData.containsKey("name"));
        assertTrue(entityData.containsKey("user"));

        assertEquals(student.getId(), entityData.get("id").getData().get());
        assertEquals(student.getName(), entityData.get("name").getData().get());
        assertEquals(student.getUser().getId(), entityData.get("user").getData().get());
    }

    @Test
    void getEmbeddableWithData() {
        Audit audit = new Audit();
        Item item = new Item();
        item.setAudit(audit);
        Map<String, AuditData> entityData = entityDataExtractor.extractAttributes(item);
        assertFalse(entityData.containsKey("audit"));
        assertTrue(entityData.containsKey("audit.createdOn"));
        assertTrue(entityData.containsKey("audit.createdBy"));
        assertTrue(entityData.containsKey("audit.updatedOn"));
        assertTrue(entityData.containsKey("audit.updatedBy"));
    }

    @Test
    void getEmbeddableWithNoData() {
        Item item = new Item();
        Map<String, AuditData> entityData = entityDataExtractor.extractAttributes(item);
        assertTrue(entityData.containsKey("audit"));
        assertFalse(entityData.get("audit").getData().isPresent());
        assertFalse(entityData.containsKey("audit.createdOn"));
        assertFalse(entityData.containsKey("audit.createdBy"));
        assertFalse(entityData.containsKey("audit.updatedOn"));
        assertFalse(entityData.containsKey("audit.updatedBy"));
    }

    @Test
    void getPrimaryKey() {
        User user = new User();
        user.setId(faker.number().randomDigit());
        assertEquals(user.getId(), entityDataExtractor.getPrimaryKey(user));
    }

    @Test
    public void getEntityBeforeOperation() {
        User user = entityManager.getReference(User.class, faker.number().randomDigit());
        assertNotEquals(User.class, user.getClass());
        EntityIdentifier identifier = entityDataExtractor.getIdentifier(user);

        EntityManager entityManager2 = Mockito.mock(EntityManager.class);
        EntityDataExtractorImpl entityDataExtractor2 = new EntityDataExtractorImpl(entityManager2) {
            @Override
            public Class<?> getType(Object e) {
                return e.getClass();
            }
        };
        entityDataExtractor2.getEntityBeforeOperation(identifier);
        Mockito.verify(entityManager2, Mockito.times(1))
                .find(User.class, user.getId());
    }
}