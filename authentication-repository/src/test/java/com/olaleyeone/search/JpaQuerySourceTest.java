package com.olaleyeone.auth.search;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.QPortalUser;
import com.olaleyeone.auth.entitytest.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JpaQuerySourceTest extends EntityTest {

    private JpaQuerySource jpaQuerySource;

    @BeforeEach
    public void setUp() {
        jpaQuerySource = applicationContext.getAutowireCapableBeanFactory().createBean(JpaQuerySource.class);
    }

    @Test
    void startJpaQuery() {
        int count = 10;
        modelFactory.create(PortalUser.class, count);
        assertEquals(count, jpaQuerySource.startQuery(QPortalUser.portalUser).fetchCount());
    }
}