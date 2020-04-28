package com.olaleyeone.entitysearch.util;

import com.olaleyeone.entitysearchtest.EntityTest;
import com.olaleyeone.entitysearchtest.data.QRecord;
import com.olaleyeone.entitysearchtest.data.Record;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;

import javax.servlet.http.HttpServletRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PredicateExtractorTest extends EntityTest {

    @Autowired
    private PredicateExtractor predicateExtractor;

    @Autowired
    private HttpServletRequest httpServletRequest;

    private QuerydslBinderCustomizer<QRecord> binderCustomizer;

    @BeforeEach
    public void setUp() {
        binderCustomizer = Mockito.mock(QuerydslBinderCustomizer.class);
    }

    @Test
    void getPredicateWithoutFilters() {
        Predicate predicate = predicateExtractor.getPredicate(binderCustomizer, Record.class);
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameterMap();
        assertNull(predicate);
    }

    @Test
    void getPredicateWithFilters() {
        Mockito.doReturn(Collections.singletonMap(QRecord.record.id.getMetadata().getName(), new String[]{"1"}))
                .when(httpServletRequest)
                .getParameterMap();
        Predicate predicate = predicateExtractor.getPredicate(binderCustomizer, Record.class);
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameterMap();
        assertNotNull(predicate);
    }
}