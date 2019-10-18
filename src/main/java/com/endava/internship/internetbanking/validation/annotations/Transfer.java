package com.endava.internship.internetbanking.validation.annotations;

import com.endava.internship.internetbanking.validation.validators.TransferValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = TransferValidator.class)
@Target({METHOD, PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface Transfer {

    String message() default "Invalid transfer object!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}