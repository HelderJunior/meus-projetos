package br.com.sebrae.sgm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import br.com.sebrae.sgm.exception.ValidateException;

public class ValidateUtils {

	/**
	 * bean validator programatically
	 * 
	 * @param obj
	 */
	public static Set<ConstraintViolation<Object>> validate(Object obj) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
		return constraintViolations;
	}

	public static List<String> validateStr(Object obj) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
		List<String> erros = new ArrayList<String>();
		for (ConstraintViolation<Object> cv : constraintViolations) {
			erros.add(cv.getMessage());
		}
		return erros;
	}
	
	
	public static void validateThrows(Object obj) throws ValidateException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
		List<String> erros = new ArrayList<String>();
		for (ConstraintViolation<Object> cv : constraintViolations) {
			erros.add(cv.getMessage());
		}
		if(!erros.isEmpty()){
			throw new ValidateException(erros);
		}
	}

}
