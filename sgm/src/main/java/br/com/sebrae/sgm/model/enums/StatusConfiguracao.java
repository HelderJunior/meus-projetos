package br.com.sebrae.sgm.model.enums;

public enum StatusConfiguracao {

	A("Andamento"), C("Conclu\u00EDdo"),N("N\u00E3o parametrizado");

	private StatusConfiguracao(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
