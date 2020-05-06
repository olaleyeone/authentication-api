package com.olaleyeone.entitysearch.util;

import com.olaleyeone.entitysearchtest.EntityTest;
import com.olaleyeone.entitysearchtest.data.EntityRecord;
import com.olaleyeone.entitysearchtest.data.QEntityRecord;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PredicateExtractorTest extends EntityTest {

    private PredicateExtractor predicateExtractor;

    private HttpServletRequest httpServletRequest;

    @Autowired
    private QuerydslBindingsFactory querydslBindingsFactory;

    private QuerydslBinderCustomizer<QEntityRecord> binderCustomizer;

    @BeforeEach
    public void setUp() {
        binderCustomizer = Mockito.mock(QuerydslBinderCustomizer.class);
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        predicateExtractor = new PredicateExtractor(querydslBindingsFactory, Optional.empty(), () -> httpServletRequest);
    }

    @Test
    void getPredicateWithoutFilters() {
        Predicate predicate = predicateExtractor.getPredicate(binderCustomizer, EntityRecord.class);
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameterMap();
        assertNull(predicate);
    }

    @Test
    void getPredicateWithFilters() {
        Mockito.doReturn(Collections.singletonMap(QEntityRecord.entityRecord.id.getMetadata().getName(), new String[]{"1"}))
                .when(httpServletRequest)
                .getParameterMap();
        Predicate predicate = predicateExtractor.getPredicate(binderCustomizer, EntityRecord.class);
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameterMap();
        assertNotNull(predicate);
    }
}