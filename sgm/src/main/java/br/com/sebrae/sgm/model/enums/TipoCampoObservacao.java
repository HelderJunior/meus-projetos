package br.com.sebrae.sgm.model.enums;

public enum TipoCampoObservacao {

	RE("Resultados Esperados"), EE("Evid\u00EAncia de Entrega");

	private TipoCampoObservacao(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
