package br.com.sebrae.sgm.model.enums;

public enum TipoAcao {

	IMT("Inserir Meta"), IMM("Inserir Monitoramento de Meta"), 
	VTM("Validar Texto de Meta"), VRM("Validar Resultado da Meta"),
	;

	private TipoAcao(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
