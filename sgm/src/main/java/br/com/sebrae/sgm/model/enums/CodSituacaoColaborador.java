package br.com.sebrae.sgm.model.enums;

public enum CodSituacaoColaborador {
	A("Ativo"), 
	D("Demitido"), 
	F("F\u00E9rias"), 
	P("Afastamento Previd\u00EAncia"), 
	E("Licen\u00E7a Maternidade"), 
	L("Licen\u00E7a sem vencimento"), 
	T("Afastado Acidente Trabalho"), 
	I("Aposentadoria Invalidez"), 
	W("Licen\u00E7a Mater. Compl. 180 dias");

	private CodSituacaoColaborador(String value) {
		this.value = value;
	}

	private final String value;

	public String getValue() {
		return value;
	}

}
