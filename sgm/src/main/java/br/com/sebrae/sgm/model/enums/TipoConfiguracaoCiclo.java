package br.com.sebrae.sgm.model.enums;

public enum TipoConfiguracaoCiclo {

	DESEMP("Meta de Desempenho"), DESENV("Meta de Desenvolvimento");

	private TipoConfiguracaoCiclo(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
