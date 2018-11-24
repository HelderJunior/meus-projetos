package br.com.sebrae.sgm.model.enums;

public enum AtivoInativo {
	A("Ativo"), I("Inativo");

	private AtivoInativo(String value) {
		this.value = value;
	}

	private final String value;

	public String getValue() {
		return value;
	}

}
