package br.com.sebrae.sgm.model.enums;

public enum TipoMeta {

	I("Desempenho Individual"), E("Desempenho Equipe"), D("Desenvolvimento");

	private TipoMeta(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
