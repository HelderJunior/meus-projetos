package br.com.sebrae.sgm.model.enums;

public enum TipoUsuario {
	I("Interno"), E("Externo");

	private final String value;

	private TipoUsuario(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
