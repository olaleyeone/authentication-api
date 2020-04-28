package com.olaleyeone.entitysearch;

import com.olaleyeone.entitysearchtest.EntityTest;
import com.olaleyeone.entitysearchtest.data.EntityRecord;
import com.olaleyeone.entitysearchtest.data.QEntityRecord;
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
            entityManager.persist(new EntityRecord());
        }
        assertEquals(count, jpaQuerySource.startQuery(QEntityRecord.entityRecord).fetchCount());
    }

    @Test
    void searchWithPage() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            entityManager.persist(new EntityRecord());
        }
        PageDto pageDto = new PageDto();
        int limit = 5;
        pageDto.setLimit(limit);
        assertEquals(limit, jpaQuerySource.setPage(jpaQuerySource.startQuery(QEntityRecord.entityRecord), pageDto).fetch().size());
    }

    @Test
    void startJpaQueryWithPredicate() {
        EntityRecord entity = new EntityRecord();
        entityManager.persist(entity);
        int count = 10;
        for (int i = 0; i < count; i++) {
            entityManager.persist(new EntityRecord());
        }
        assertEquals(1, jpaQuerySource.startQuery(
                QEntityRecord.entityRecord,
                QEntityRecord.entityRecord.id.eq(entity.getId())).fetch().size());
    }
}