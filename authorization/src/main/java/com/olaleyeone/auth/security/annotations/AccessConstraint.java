package com.olaleyeone.auth.security.annotations;

import com.olaleyeone.auth.security.access.AccessStatusSource;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
@Documented
public @interface AccessConstraint {

    Class<? extends AccessStatusSource<?>> value();
}
