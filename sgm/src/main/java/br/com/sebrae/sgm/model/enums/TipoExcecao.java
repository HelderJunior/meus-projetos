package br.com.sebrae.sgm.model.enums;

public enum TipoExcecao {

	IMM("Inserir Metas e Monitoramento"), VMR("Validar Metas e Resultados");

	private TipoExcecao(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
