package com.olaleyeone.auth.constraints;

import com.olaleyeone.auth.data.dto.UserRegistrationApiRequest;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE_USE})
@Constraint(validatedBy = {HasName.Validator.class})
public @interface HasName {

    String message() default "{com.olaleyeone.auth.HasName.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value() default "";

    interface Validator extends ConstraintValidator<HasName, UserRegistrationApiRequest> {
    }
}
