package br.com.sebrae.sgm.model.enums;

public enum StatusExecucaoMeta {

	NC("Meta N\u00E3o Cumprida"), CP("Meta Cumprida Parcialmente"), MC("Meta Cumprida");

	private StatusExecucaoMeta(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
