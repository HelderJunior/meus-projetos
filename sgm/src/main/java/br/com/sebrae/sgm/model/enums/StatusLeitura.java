package br.com.sebrae.sgm.model.enums;

public enum StatusLeitura {

	L("Lido"), N("N\u00E3o Lido");

	private final String value;

	private StatusLeitura(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
