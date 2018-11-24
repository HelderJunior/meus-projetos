package br.com.sebrae.sgm.model.enums;

public enum TipoMetaAcessoAdministrador {

	P("Desempenho"), D("Desenvolvimento"), A("Ambas") ;

	private TipoMetaAcessoAdministrador(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
