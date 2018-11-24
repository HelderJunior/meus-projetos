package br.com.sebrae.sgm.model.enums;

public enum TipoAcaoAvaliador {

	AMM("An\u00E1lise Metodol\u00F3gica da Meta"), VCP("Validar Comprova\u00E7\u00E3o");

	private TipoAcaoAvaliador(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
