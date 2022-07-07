package com.exrade.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author Md. Aslam Hossain
 * @Date Jun 27, 2022
 */


@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProfileSubDomain.class)
public @interface ProfileSubDomainValidator {

    String message() default "Subdomain should not be duplicate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
