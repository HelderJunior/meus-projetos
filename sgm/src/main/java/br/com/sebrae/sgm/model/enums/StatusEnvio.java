package br.com.sebrae.sgm.model.enums;

public enum StatusEnvio {

	E("Enviado"), N("N\u00E3o Enviado");

	private final String value;

	private StatusEnvio(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
