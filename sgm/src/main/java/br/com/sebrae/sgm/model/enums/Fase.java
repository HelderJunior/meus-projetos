package br.com.sebrae.sgm.model.enums;

public enum Fase {

	P("Pactua\u00E7\u00E3o"), 
	R("Repactua\u00E7\u00E3o"), 
	M("Monitoramento"), 
	J("Ajustes"), 
	V("Valida\u00E7\u00E3o"), 
	A("Auditoria");

	private Fase(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
