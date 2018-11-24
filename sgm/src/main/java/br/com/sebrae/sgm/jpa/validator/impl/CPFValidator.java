package br.com.sebrae.sgm.jpa.validator.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.sebrae.sgm.jpa.validator.CPF;
import br.com.sebrae.sgm.utils.CPFUtils;

public class CPFValidator implements ConstraintValidator<CPF, String> {

	@Override
	public void initialize(CPF constraintAnnotation) {
	}

	@Override
	public boolean isValid(String cpf, ConstraintValidatorContext context) {
		return CPFUtils.validate(cpf);
	}

}
