package com.olaleyeone.auth.search.filter;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.QPortalUserAuthentication;
import com.olaleyeone.entitysearch.PageDto;
import com.olaleyeone.entitysearch.SearchFilter;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.time.LocalDateTime;

@Data
public class PortalUserAuthenticationSearchFilter implements SearchFilter<PortalUserAuthentication, QPortalUserAuthentication> {

    @Hidden
    @Delegate(types = PageDto.class)
    private PageDto pager = new PageDto();

    @Hidden
    @Delegate(excludes = Exclude.class)
    private PortalUserAuthentication portalUserAuthentication = new PortalUserAuthentication();

    @Override
    public void customize(QuerydslBindings bindings, QPortalUserAuthentication root) {
        //noop
    }

    @Getter
    @Setter
    private static class Exclude {

        private PortalUserIdentifier portalUserIdentifier;
        private PortalUser portalUser;
        private LocalDateTime becomesInactiveAt;
        private LocalDateTime autoLogoutAt;
        private LocalDateTime loggedOutAt;
        private LocalDateTime deactivatedAt;
        private LocalDateTime lastAuthorizedAt;
    }
}
