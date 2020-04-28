package com.olaleyeone.entitysearch;

import com.olaleyeone.entitysearchtest.EntityTest;
import com.olaleyeone.entitysearchtest.data.QRecord;
import com.olaleyeone.entitysearchtest.data.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
class JpaQuerySourceTest extends EntityTest {

    private JpaQuerySource jpaQuerySource;

    @BeforeEach
    public void setUp() {
        jpaQuerySource = new JpaQuerySource(entityManager);
    }

    @Test
    void startJpaQuery() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            entityManager.persist(new Record());
        }
        assertEquals(count, jpaQuerySource.startQuery(QRecord.record).fetchCount());
    }

    @Test
    void searchWithPage() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            entityManager.persist(new Record());
        }
        PageDto pageDto = new PageDto();
        int limit = 5;
        pageDto.setLimit(limit);
        assertEquals(limit, jpaQuerySource.setPage(jpaQuerySource.startQuery(QRecord.record), pageDto).fetch().size());
    }

    @Test
    void startJpaQueryWithPredicate() {
        Record entity = new Record();
        entityManager.persist(entity);
        int count = 10;
        for (int i = 0; i < count; i++) {
            entityManager.persist(new Record());
        }
        assertEquals(1, jpaQuerySource.startQuery(QRecord.record, QRecord.record.id.eq(entity.getId())).fetch().size());
    }
}