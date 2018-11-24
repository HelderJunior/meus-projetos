package br.com.sebrae.sgm.model.enums;

public enum Mes {

	JAN("Janeiro"), FEV("Fevereiro"), MAR("Mar\u00E7o"), ABR("Abril"), MAI("Maio"), JUN("Junho"), JUL("Julho"), AGO("Agosto"), SET(
			"Setembro"), OUT("Outubro"), NOV("Novembro"), DEZ("Dezembro");

	private final String value;

	private Mes(String name) {
		this.value = name;
	}

	public String getValue() {
		return value;
	}

}
