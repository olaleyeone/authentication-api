package com.olaleyeone.entitysearch.util;

import com.olaleyeone.entitysearch.SearchFilter;
import com.olaleyeone.entitysearchtest.EntityTest;
import com.olaleyeone.entitysearchtest.data.QEntityRecord;
import com.olaleyeone.entitysearchtest.data.EntityRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.querydsl.binding.QuerydslBindings;

class SearchFilterPredicateExtractorTest extends EntityTest {

    private SearchFilterPredicateExtractor searchFilterPredicateExtractor;

    private PredicateExtractor predicateExtractor;
    private SearchFilter<EntityRecord, QEntityRecord> binderCustomizer;

    @BeforeEach
    void setUp() {
        predicateExtractor = Mockito.mock(PredicateExtractor.class);
        binderCustomizer = new SearchFilter<EntityRecord, QEntityRecord>() {

            @Override
            public void customize(QuerydslBindings bindings, QEntityRecord root) {
                //No op
            }
        };
        searchFilterPredicateExtractor = new SearchFilterPredicateExtractor(predicateExtractor);
    }

    @Test
    void getPredicate() {
        searchFilterPredicateExtractor.getPredicate(binderCustomizer);
        Mockito.verify(predicateExtractor, Mockito.times(1))
                .getPredicate(binderCustomizer, EntityRecord.class);
    }
}