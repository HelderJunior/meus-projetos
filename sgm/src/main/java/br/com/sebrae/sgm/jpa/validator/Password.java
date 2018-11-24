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

import br.com.sebrae.sgm.jpa.validator.impl.PasswordValidator;

@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface Password {
	
	
	String message() default "A senha deve conter no m\u00EDnimo oito caracteres, devendo ter letras e n\u00FAmeros e pelo menos uma letra mai\u00FAscula.";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
	
	/**
        (?=.*[0-9]) a digit must occur at least once
		(?=.*[a-z]) a lower case letter must occur at least once
		(?=.*[A-Z]) an upper case letter must occur at least once
		(?=.*[@#*=]) a special character must occur at least once
		(?=[\\S]+$) no whitespace allowed in the entire string
		.{5,10} at least 5 to 10 characters
	 */
	String value() default "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20})";

}
