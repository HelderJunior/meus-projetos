package br.com.sebrae.sgm.model.enums;

public enum StatusCiclo {

	I("Iniciado"), N("N\u00E3o Iniciado"), F("Finalizado");

	private StatusCiclo(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
