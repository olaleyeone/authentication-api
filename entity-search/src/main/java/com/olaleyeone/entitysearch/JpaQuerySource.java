package com.olaleyeone.entitysearch;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequiredArgsConstructor
public class JpaQuerySource {

    private final EntityManager entityManager;

    public <E, Q extends EntityPath<E>> JPAQuery<E> startQuery(Q q) {
        return startQuery(q, null);
    }

    public <E, Q extends EntityPath<E>> JPAQuery<E> startQuery(Q q, Predicate predicate) {
        JPAQuery<E> jpaQuery = new JPAQuery<E>(entityManager).from(q);
        if (predicate != null) {
            jpaQuery.where(predicate);
        }
        return jpaQuery;
    }

    public <E> JPAQuery<E> setPage(JPAQuery<E> jpaQuery, PageDto pageDto) {
        pageDto.getLimit().ifPresent(limit -> jpaQuery.limit(limit).offset(pageDto.getOffset().orElse(0)));
        return jpaQuery;
    }
}
