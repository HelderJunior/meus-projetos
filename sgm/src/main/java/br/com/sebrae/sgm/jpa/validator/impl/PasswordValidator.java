package br.com.sebrae.sgm.jpa.validator.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.sebrae.sgm.jpa.validator.Password;

public class PasswordValidator implements ConstraintValidator<Password, String> {

	private String pattern;

	@Override
	public void initialize(Password constraintAnnotation) {
		this.pattern = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String passwd, ConstraintValidatorContext context) {
		if (passwd != null)
			return passwd.matches(pattern);
		return true;
	}

}
