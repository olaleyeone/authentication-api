package com.github.olaleyeone.entitysearch.util;

import com.github.olaleyeone.entitysearch.SearchFilter;
import com.github.olaleyeone.test.entity.EntityTest;
import com.github.olaleyeone.test.entity.data.EntityRecord;
import com.github.olaleyeone.test.entity.data.QEntityRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

    @Test
    void getPredicateWithParams() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        searchFilterPredicateExtractor.getPredicate(binderCustomizer, parameters);
        Mockito.verify(predicateExtractor, Mockito.times(1))
                .getPredicate(binderCustomizer, parameters, EntityRecord.class);
    }
}