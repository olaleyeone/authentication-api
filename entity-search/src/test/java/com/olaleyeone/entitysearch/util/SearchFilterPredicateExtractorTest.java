package com.olaleyeone.entitysearch.util;

import com.olaleyeone.entitysearch.SearchFilter;
import com.olaleyeone.entitysearchtest.EntityTest;
import com.olaleyeone.entitysearchtest.data.QRecord;
import com.olaleyeone.entitysearchtest.data.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import static org.junit.jupiter.api.Assertions.*;

class SearchFilterPredicateExtractorTest extends EntityTest {

    private SearchFilterPredicateExtractor searchFilterPredicateExtractor;

    private PredicateExtractor predicateExtractor;
    private SearchFilter<Record, QRecord> binderCustomizer;

    @BeforeEach
    void setUp() {
        predicateExtractor = Mockito.mock(PredicateExtractor.class);
        binderCustomizer = new SearchFilter<Record, QRecord>() {

            @Override
            public void customize(QuerydslBindings bindings, QRecord root) {
                //No op
            }
        };
        searchFilterPredicateExtractor = new SearchFilterPredicateExtractor(predicateExtractor);
    }

    @Test
    void getPredicate() {
        searchFilterPredicateExtractor.getPredicate(binderCustomizer);
        Mockito.verify(predicateExtractor, Mockito.times(1))
                .getPredicate(binderCustomizer, Record.class);
    }
}