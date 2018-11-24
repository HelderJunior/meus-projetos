package br.com.sebrae.sgm.model.enums;

public enum TipoDado {

	P("Percentual"), I("Inteiro"), D("Decimal");

	private TipoDado(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
