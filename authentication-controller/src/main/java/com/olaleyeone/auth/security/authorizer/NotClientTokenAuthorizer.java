package com.olaleyeone.auth.security.authorizer;

import com.github.olaleyeone.auth.access.AccessStatus;
import com.github.olaleyeone.auth.access.Authorizer;
import com.github.olaleyeone.auth.data.AccessClaims;
import com.olaleyeone.auth.security.constraint.NotClientToken;

import javax.inject.Named;

@Named
public class NotClientTokenAuthorizer implements Authorizer<NotClientToken> {

    @Override
    public AccessStatus getStatus(NotClientToken accessConstraint, AccessClaims claims) {
        return claims.getAudience().isEmpty() ? AccessStatus.allowed() : AccessStatus.denied();
    }
}
