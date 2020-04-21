package com.olaleyeone.auth.security.authorizer;

import com.olaleyeone.auth.security.access.AccessStatus;
import com.olaleyeone.auth.security.access.Authorizer;
import com.olaleyeone.auth.security.constraint.NotClientToken;
import com.olaleyeone.auth.security.data.AccessClaims;

import javax.inject.Named;

@Named
public class NotClientTokenAuthorizer implements Authorizer<NotClientToken> {

    @Override
    public AccessStatus getStatus(NotClientToken accessConstraint, AccessClaims claims) {
        return claims.getAudience().isEmpty() ? AccessStatus.allowed() : AccessStatus.denied();
    }
}
