package br.com.sebrae.sgm.model.enums;

public enum VinculoOcupacional {
	F("Fundamental"), C("Complementar");

	private VinculoOcupacional(String value) {
		this.value = value;
	}

	private final String value;

	public String getValue() {
		return value;
	}

}
