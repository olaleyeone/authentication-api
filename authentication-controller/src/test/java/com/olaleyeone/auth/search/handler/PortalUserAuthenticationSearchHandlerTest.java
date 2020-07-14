package com.olaleyeone.auth.search.handler;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.test.entity.EntityTest;
import com.olaleyeone.auth.search.filter.PortalUserAuthenticationSearchFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PortalUserAuthenticationSearchHandlerTest extends EntityTest {

    private PortalUserAuthenticationSearchHandler searchHandler;
    private PortalUserAuthenticationSearchFilter searchFilter;

    @BeforeEach
    void setUp() {
        searchHandler = applicationContext.getAutowireCapableBeanFactory().createBean(PortalUserAuthenticationSearchHandler.class);
        searchFilter = new PortalUserAuthenticationSearchFilter();
    }

    @Test
    void search() {
        int count = 5;
        modelFactory.create(PortalUserAuthentication.class, count);
        assertEquals(count, searchHandler.search(searchFilter, null).getTotal());
    }
}