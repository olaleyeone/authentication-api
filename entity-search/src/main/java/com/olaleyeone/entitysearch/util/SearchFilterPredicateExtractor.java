package com.olaleyeone.entitysearch.util;

import com.olaleyeone.entitysearch.SearchFilter;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

@RequiredArgsConstructor
public class SearchFilterPredicateExtractor {

    private final PredicateExtractor predicateExtractor;

    public <E, Q extends EntityPath<E>> Predicate getPredicate(SearchFilter<E, Q> binderCustomizer) {
        return predicateExtractor.getPredicate(binderCustomizer, getEntityType(binderCustomizer));
    }

    private static <E, Q extends EntityPath<E>> Class<E> getEntityType(SearchFilter<E, Q> binderCustomizer) {
        ParameterizedType bType = Arrays.asList(binderCustomizer.getClass().getGenericInterfaces())
                .stream()
                .filter(it -> it instanceof ParameterizedType)
                .map(it -> (ParameterizedType) it)
                .findFirst()
                .get();

        Class<Q> qType = (Class<Q>) bType.getActualTypeArguments()[1];
        return (Class<E>) ((ParameterizedType) qType.getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
