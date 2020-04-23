package com.olaleyeone.audittrail.impl;

import com.olalayeone.audittrailtest.EntityTest;
import com.olaleyeone.audittrail.advice.AuditTrailAdvice;
import com.olaleyeone.audittrail.api.*;
import com.olaleyeone.audittrail.entity.EntityState;
import com.olaleyeone.audittrail.entity.EntityStateAttribute;
import com.olaleyeone.audittrail.entity.RequestLog;
import com.olaleyeone.audittrail.entity.UnitOfWork;
import com.olaleyeone.audittrail.repository.EntityStateAttributeRepository;
import com.olaleyeone.audittrail.repository.EntityStateRepository;
import com.olaleyeone.audittrail.repository.UnitOfWorkRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UnitOfWorkLoggerTest extends EntityTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UnitOfWorkRepository unitOfWorkRepository;

    @Autowired
    private EntityStateRepository entityStateRepository;

    @Autowired
    private EntityStateAttributeRepository entityStateAttributeRepository;

    @Autowired
    private AuditTrailAdvice auditTrailAdvice;

    private UnitOfWorkLogger unitOfWorkLogger;

    private EntityStateLogger entityStateLogger;

    private EntityIdentifier entityIdentifier;
    private EntityType<RequestLog> entityType;

    private RequestLog requestLog;

    @BeforeEach
    void setUp() {
        entityStateLogger = Mockito.mock(EntityStateLogger.class);
        requestLog = new RequestLog();
        requestLog.setSessionId(faker.number().digit());
        entityManager.persist(requestLog);
        unitOfWorkLogger = new UnitOfWorkLogger(entityStateLogger, entityManager) {

            @Override
            public Optional<RequestLog> getRequest() {
                return Optional.of(requestLog);
            }
        };
        entityType = entityManager.getEntityManagerFactory().getMetamodel().entity(RequestLog.class);
        entityIdentifier = new EntityIdentifierImpl(entityType, faker.number().randomDigit());
    }

    @AfterEach
    public void afterEach() throws Throwable {
        Mockito.verify(auditTrailAdvice, Mockito.never()).adviceEntityCreation(Mockito.any());
    }

    @Test
    void saveUnitOfWork() {

        List<EntityOperation> entityOperations = getEntityHistoryLogs();
        Mockito.doReturn(entityOperations).when(entityStateLogger).getOperations();
        unitOfWorkLogger.beforeCommit(false);
        assertEquals(1, unitOfWorkRepository.count());
        assertEquals(3, entityStateRepository.count());
        assertEquals(3, entityStateAttributeRepository.count());

        List<UnitOfWork> units = unitOfWorkRepository.getAllByRequest(requestLog);

        assertEquals(1, units.size());
        UnitOfWork auditTrail = units.iterator().next();
        entityOperations.forEach(entityHistoryLog -> {
            EntityIdentifier entityIdentifier = entityHistoryLog.getEntityIdentifier();
            Optional<EntityState> optionalEntityHistory = entityStateRepository.getByUnitOfWork(auditTrail, entityIdentifier.getEntityName(),
                    entityIdentifier.getPrimaryKey().toString());
            assertTrue(optionalEntityHistory.isPresent());
            EntityState entityState = optionalEntityHistory.get();
            entityHistoryLog.getAttributes().entrySet()
                    .forEach(entry -> assertTrue(entityStateAttributeRepository.getByEntityHistory(entityState, entry.getKey()).isPresent()));
        });
    }

    @Test
    void shouldIgnoreNoUpdate() {

        Mockito.doReturn(Collections.EMPTY_LIST).when(entityStateLogger).getOperations();
        unitOfWorkLogger.beforeCommit(false);
        assertEquals(0, unitOfWorkRepository.count());
    }

    @Test
    void saveEntityHistory() {
        UnitOfWork auditTrail = createUnitOfWork();
        OperationType operationType = OperationType.CREATE;
        EntityOperation historyLog = new EntityOperation(entityIdentifier, operationType);
        EntityState entityState = unitOfWorkLogger.createEntityHistory(auditTrail, historyLog);
        assertNotNull(entityState);
        assertNotNull(entityState.getId());
        assertEquals(auditTrail, entityState.getUnitOfWork());
        assertEquals(operationType, entityState.getOperationType());

        assertEquals(entityType.getName(), entityState.getEntityName());
        assertEquals(entityIdentifier.getPrimaryKey().toString(), entityState.getEntityId());
    }

    @Test
    void saveEntityHistoryAttribute() {
        EntityState entityState = createEntityHistory();

        EntityAttributeData data = EntityAttributeData.builder()
                .value(new AuditDataImpl(faker.lordOfTheRings().character()))
                .previousValue(new AuditDataImpl(faker.lordOfTheRings().character()))
                .build();

        EntityStateAttribute attribute = unitOfWorkLogger.createEntityHistoryAttribute(entityState, Pair.of(faker.funnyName().name(), data));
        assertNotNull(attribute);
        assertNotNull(attribute.getId());
        assertEquals(entityState, attribute.getEntityState());
        assertEquals(data.isModified(), attribute.isModified());
        assertEquals(data.getPreviousValue().getTextValue().get(), attribute.getPreviousValue());
        assertEquals(data.getValue().getTextValue().get(), attribute.getValue());
    }

    private List<EntityOperation> getEntityHistoryLogs() {
        EntityType<?> entityType = entityManager.getEntityManagerFactory().getMetamodel().getEntities().iterator().next();
        List<EntityOperation> entityOperations = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            EntityOperation historyLog = new EntityOperation(new EntityIdentifierImpl(entityType, i), OperationType.CREATE);
            Map<String, EntityAttributeData> dataMap = new HashMap<>();
            for (int j = 0; j < i; j++) {
                EntityAttributeData data = EntityAttributeData.builder()
                        .value(new AuditDataImpl(faker.lordOfTheRings().character()))
                        .previousValue(new AuditDataImpl(faker.lordOfTheRings().character()))
                        .build();
                dataMap.put(faker.funnyName().name(), data);
            }
            historyLog.setAttributes(dataMap);
            entityOperations.add(historyLog);
        }
        return entityOperations;
    }

    private UnitOfWork createUnitOfWork() {
        UnitOfWork unitOfWork = new UnitOfWork();
        unitOfWork.setEstimatedTimeTakenInNanos(faker.number().randomNumber());
        entityManager.persist(unitOfWork);
        return unitOfWork;
    }

    private EntityState createEntityHistory() {
        EntityState entityState = new EntityState();
        entityState.setUnitOfWork(createUnitOfWork());
        entityState.setOperationType(OperationType.CREATE);
        entityState.setEntityName(faker.funnyName().name());
        entityState.setEntityId(faker.number().digit());
        entityManager.persist(entityState);
        return entityState;
    }
}