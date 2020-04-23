package com.olaleyeone.entitysearch;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;

public interface SearchHandler<E, F extends SearchFilter<E, ?>> {

    QueryResults<E> search(F filter, Predicate predicate);
}
