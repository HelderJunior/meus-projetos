package br.com.sebrae.sgm.model.enums;

public enum PeriodicidadeEnvio {

	D("Di\u00E1rio"), S("Semanal"), AP("A cada Pend\u00EAncia"), ME("Mensal");

	private PeriodicidadeEnvio(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
