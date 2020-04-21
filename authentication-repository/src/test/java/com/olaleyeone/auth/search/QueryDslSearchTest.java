package com.olaleyeone.auth.search;

import com.olaleyeone.auth.test.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueryDslSearchTest extends EntityTest {

    private QueryDslSearch queryDslSearch;

    @BeforeEach
    public void setUp(){
        queryDslSearch = applicationContext.getAutowireCapableBeanFactory().createBean(QueryDslSearch.class);
    }

    @Test
    void getAllSettings() {
        queryDslSearch.getAllSettings();
    }
}