package br.com.sebrae.sgm.model.enums;

public enum StatusSolucaoEducacional {

	N("N\u00E3o Validado"), 
	P("Pendente"), 
	V("Validado");

	private StatusSolucaoEducacional(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
