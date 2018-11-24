package br.com.sebrae.sgm.jpa.validator.impl;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.sebrae.sgm.utils.DateUtils;

public class DateValidator implements
		ConstraintValidator<br.com.sebrae.sgm.jpa.validator.Date, Date> {

	@Override
	public void initialize(
			br.com.sebrae.sgm.jpa.validator.Date constraintAnnotation) {
	}

	@Override
	public boolean isValid(Date date, ConstraintValidatorContext context) {

		if (date != null) {
			Calendar dataMinima = Calendar.getInstance();
			dataMinima.set(Calendar.YEAR, 1753);
			dataMinima.set(Calendar.MONTH, Calendar.JANUARY);
			dataMinima.set(Calendar.DAY_OF_MONTH, 1);

			Calendar dataMaxima = Calendar.getInstance();
			dataMaxima.set(Calendar.YEAR, 9999);
			dataMaxima.set(Calendar.MONTH, Calendar.DECEMBER);
			dataMaxima.set(Calendar.DAY_OF_MONTH, 31);

			if ((DateUtils.getDaysDiff(dataMinima.getTime(), date)
					.compareTo(0L) < 0)
					|| (DateUtils.getDaysDiff(date, dataMaxima.getTime())
							.compareTo(0L) < 0)) {
				return false;
			}
		}

		return true;
	}

}
