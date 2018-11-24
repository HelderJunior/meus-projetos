package br.com.sebrae.sgm.model.enums;

public enum TipoGrupo {

	G("Gerente"), C("Colaborador"), E("Equipe");

	private TipoGrupo(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
