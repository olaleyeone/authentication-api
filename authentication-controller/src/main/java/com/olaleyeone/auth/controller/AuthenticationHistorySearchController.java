package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.search.filter.PortalUserAuthenticationSearchFilter;
import com.olaleyeone.auth.search.handler.PortalUserAuthenticationSearchHandler;
import com.olaleyeone.entitysearch.util.SearchFilterPredicateExtractor;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthenticationHistorySearchController {

    private final PortalUserAuthenticationSearchHandler searchHandler;
    private final SearchFilterPredicateExtractor predicateExtractor;

    @GetMapping("/me/sessions")
    public QueryResults<PortalUserAuthentication> searchUserSessions(PortalUserAuthenticationSearchFilter filter) {
        return searchHandler.search(filter, predicateExtractor.getPredicate(filter));
    }
}
