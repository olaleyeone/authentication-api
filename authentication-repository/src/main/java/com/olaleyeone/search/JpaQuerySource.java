package com.olaleyeone.auth.search;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class JpaQuerySource {

    @PersistenceContext
    private EntityManager entityManager;

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
