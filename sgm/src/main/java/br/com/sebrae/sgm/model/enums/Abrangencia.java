package br.com.sebrae.sgm.model.enums;

public enum Abrangencia {

	N("Nacional"), L("Local");

	private Abrangencia(String value) {
		this.value = value;
	}

	private final String value;

	public String getValue() {
		return value;
	}

}
