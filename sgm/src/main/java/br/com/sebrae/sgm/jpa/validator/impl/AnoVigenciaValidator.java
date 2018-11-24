package br.com.sebrae.sgm.jpa.validator.impl;

import java.util.Calendar;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.sebrae.sgm.jpa.validator.AnoVigencia;

public class AnoVigenciaValidator implements ConstraintValidator<AnoVigencia, Integer> {

	@Override
	public void initialize(AnoVigencia constraintAnnotation) {
	}

	@Override
	public boolean isValid(Integer ano, ConstraintValidatorContext context) {
		Integer anoAtual = Calendar.getInstance().get(Calendar.YEAR);

		if (anoAtual.compareTo(ano) > 0) {
			return false;
		}
		
		return true;
	}

}
