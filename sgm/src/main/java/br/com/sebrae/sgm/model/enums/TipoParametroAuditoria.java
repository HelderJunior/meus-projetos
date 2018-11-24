package br.com.sebrae.sgm.model.enums;

public enum TipoParametroAuditoria {

	E("Metas de Equipes Auditadas"), C("Colaboradores Auditados");

	private TipoParametroAuditoria(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
