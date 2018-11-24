package br.com.sebrae.sgm.model.enums;

public enum StatusMeta {

	PC("Pendente Cancelamento"),
	NA("N\u00E3o Aprovado Superior"), 
	CS("Cancelado Superior"), 
	PV("Pendente Valida\u00E7\u00E3o"), 
	VC("Validado Comit\u00EA"), 
	LC("Lido Comit\u00EA"),
	OA("Observa\u00E7\u00E3o Auditor"),
	AA("Atestado Auditor"),
	
	
	OU("Observa\u00E7\u00E3o UGP"),
	PAC("Pendente Avali\u00E7\u00E3o Comit\u00EA"),
	PU("Pendente Avalia\u00E7\u00E3o UGP"),
	AP("Meta Aprovada"),
	AC("Meta Avali\u00E7\u00E3o Comit\u00EA"),
	AU("Meta Avali\u00E7\u00E3o UGP"),
	SA("Meta Gravada"),
	AS("Aprovado Superior"),
	OS("Observa\u00E7\u00E3o Superior"),
	OC("Observa\u00E7\u00E3o Comit\u00EA"),
	PA("Pendente Aprova\u00E7\u00E3o");

	private StatusMeta(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
