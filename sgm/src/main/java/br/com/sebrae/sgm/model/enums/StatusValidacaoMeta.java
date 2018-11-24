package br.com.sebrae.sgm.model.enums;

public enum StatusValidacaoMeta {

	NEV("N\u00E3o Enviada para Valida\u00E7\u00E3o"), 
	EPV("Enviada para Valida\u00E7\u00E3o"), 
	VLD("Validada"), 
	NLD("N\u00E3o Validada"), 
	COS("Com Observa\u00E7\u00E3o Superior");

	private StatusValidacaoMeta(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
