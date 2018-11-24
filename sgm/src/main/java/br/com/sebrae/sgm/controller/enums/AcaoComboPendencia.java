package br.com.sebrae.sgm.controller.enums;

public enum AcaoComboPendencia {

	EA("Enviar para Aprova\u00E7\u00E3o"), EX("Excluir"), CA("Cancelar");

	private AcaoComboPendencia(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
