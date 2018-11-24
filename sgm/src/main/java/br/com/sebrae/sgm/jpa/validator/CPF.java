package br.com.sebrae.sgm.jpa.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.sebrae.sgm.jpa.validator.impl.CPFValidator;

@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CPFValidator.class)
@Documented
public @interface CPF {

	String message() default "CPF inv\u00E1lido";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
