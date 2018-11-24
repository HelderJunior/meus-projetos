package br.com.sebrae.sgm.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidateException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> msgErrors = new ArrayList<String>();
	
	
	public ValidateException(String msg) {
		super(msg);
		msgErrors.add(msg);
	}
	
	public ValidateException(List<String> msg) {
		msgErrors.addAll(msg);
	}

	public List<String> getMsgErrors() {
		return msgErrors;
	}

}
