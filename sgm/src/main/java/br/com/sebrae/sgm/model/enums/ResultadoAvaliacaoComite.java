package br.com.sebrae.sgm.model.enums;

public enum ResultadoAvaliacaoComite {

	F("Fraco"), R("Regular"), T("Forte");

	private final String value;

	private ResultadoAvaliacaoComite(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
