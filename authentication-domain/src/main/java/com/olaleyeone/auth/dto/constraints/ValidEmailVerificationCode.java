package com.olaleyeone.auth.dto.constraints;

import com.olaleyeone.auth.dto.data.UserRegistrationApiRequest;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE_USE})
@Constraint(validatedBy = {ValidEmailVerificationCode.Validator.class})
public @interface ValidEmailVerificationCode {

    String message() default "{com.olaleyeone.auth.ValidEmailVerificationCode.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value() default "";

    interface Validator extends ConstraintValidator<ValidEmailVerificationCode, UserRegistrationApiRequest> {
    }
}
