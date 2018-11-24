package br.com.sebrae.sgm.model.enums;

public enum TipoObservacao {

	G("Gerente"), C("Comit\u00EA"), U("UGP"), A("Auditor"), M("Monitoramento");

	private TipoObservacao(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
