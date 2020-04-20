package com.olaleyeone.auth.security.access;

import java.lang.annotation.Annotation;

public interface AccessStatusSource<A extends Annotation> {

    AccessStatus getStatus(A accessConstraint);
}
