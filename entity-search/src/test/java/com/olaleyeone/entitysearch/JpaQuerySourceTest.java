package com.olaleyeone.entitysearch;

import com.olaleyeone.entitysearch.data.QStore;
import com.olaleyeone.entitysearch.data.Store;
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
            entityManager.persist(new Store());
        }
        assertEquals(count, jpaQuerySource.startQuery(QStore.store).fetchCount());
    }

    @Test
    void searchWithPage() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            entityManager.persist(new Store());
        }
        PageDto pageDto = new PageDto();
        int limit = 5;
        pageDto.setLimit(limit);
        assertEquals(limit, jpaQuerySource.setPage(jpaQuerySource.startQuery(QStore.store), pageDto).fetch().size());
    }

    @Test
    void startJpaQueryWithPredicate() {
        Store entity = new Store();
        entityManager.persist(entity);
        int count = 10;
        for (int i = 0; i < count; i++) {
            entityManager.persist(new Store());
        }
        assertEquals(1, jpaQuerySource.startQuery(QStore.store, QStore.store.id.eq(entity.getId())).fetch().size());
    }
}