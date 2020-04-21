package com.olaleyeone.auth.security.access;

import com.olaleyeone.auth.security.data.AccessClaims;

import java.lang.annotation.Annotation;

public interface Authorizer<A extends Annotation> {

    AccessStatus getStatus(A accessConstraint, AccessClaims requestMetadata);
}
