package com.olaleyeone.auth.security.constraint;

import com.github.olaleyeone.auth.annotations.AccessConstraint;
import com.olaleyeone.auth.security.authorizer.NotClientTokenAuthorizer;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@AccessConstraint(NotClientTokenAuthorizer.class)
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface NotClientToken {
}
