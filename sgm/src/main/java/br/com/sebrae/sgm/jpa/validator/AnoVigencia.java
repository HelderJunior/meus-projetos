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

import br.com.sebrae.sgm.jpa.validator.impl.AnoVigenciaValidator;

@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AnoVigenciaValidator.class)
@Documented
public @interface AnoVigencia {

	String message() default "Ano de vig\u00EAncia n\u00E3o pode ser menor que o ano atual";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
