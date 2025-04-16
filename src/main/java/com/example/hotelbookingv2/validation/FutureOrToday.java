package com.example.hotelbookingv2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = FutureOrTodayValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrToday {
    String message() default "Дата должна быть сегодняшней или в будущем";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

