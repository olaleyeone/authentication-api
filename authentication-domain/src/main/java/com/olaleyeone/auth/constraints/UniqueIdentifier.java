/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olaleyeone.auth.constraints;

import com.olaleyeone.auth.data.enums.UserIdentifierType;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE_USE})
@Constraint(validatedBy = {UniqueIdentifier.Validator.class})
public @interface UniqueIdentifier {

    String message() default "{com.olaleyeone.auth.validation.UniqueIdentifier.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    UserIdentifierType value();

    interface Validator extends ConstraintValidator<UniqueIdentifier, String> {
    }
}
