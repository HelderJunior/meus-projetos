package br.com.sebrae.sgm.model.enums;

public enum TipoLog {

	INC("Inclus\u00E3o"), EDI("Edi\u00E7\u00E3o"), EXC("Exclus\u00E3o"), 
	EVA("Enviar para Aprova\u00E7\u00E3o"), OBS("Observa\u00E7\u00E3o"),
	APR("Aprova\u00E7\u00E3o"), CAM("Cancelamento");

	private TipoLog(String value) {
		this.value = value;
	}

	final String value;

	public String getValue() {
		return value;
	}

}
