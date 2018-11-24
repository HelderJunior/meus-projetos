package br.com.sebrae.sgm.model.enums;

public enum FormaCalculo {

	C("Cumulativa"), N("N\u00E3o Cumulativa"), M("M\u00E9dia");

	private FormaCalculo(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
