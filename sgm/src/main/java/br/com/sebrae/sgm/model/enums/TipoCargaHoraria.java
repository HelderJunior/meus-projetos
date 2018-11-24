package br.com.sebrae.sgm.model.enums;

public enum TipoCargaHoraria {

	U("UC"), F("Fixa"), V("Vari\u00E1vel");

	private TipoCargaHoraria(String value) {
		this.value = value;
	}

	private final String value;

	public String getValue() {
		return value;
	}

}
