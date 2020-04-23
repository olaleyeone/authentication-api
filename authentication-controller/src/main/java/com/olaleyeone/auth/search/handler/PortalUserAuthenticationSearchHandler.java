package com.olaleyeone.auth.search.handler;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.QPortalUserAuthentication;
import com.olaleyeone.auth.search.filter.PortalUserAuthenticationSearchFilter;
import com.olaleyeone.entitysearch.JpaQuerySource;
import com.olaleyeone.entitysearch.SearchHandler;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class PortalUserAuthenticationSearchHandler implements SearchHandler<PortalUserAuthentication, PortalUserAuthenticationSearchFilter> {

    @Inject
    private JpaQuerySource jpaQuerySource;

    @Override
    public QueryResults<PortalUserAuthentication> search(PortalUserAuthenticationSearchFilter filter, Predicate predicate) {
        JPAQuery<PortalUserAuthentication> jpaQuery = jpaQuerySource.startQuery(QPortalUserAuthentication.portalUserAuthentication, predicate);
        return jpaQuerySource.setPage(jpaQuery, filter.getPager()).fetchResults();
    }
}
