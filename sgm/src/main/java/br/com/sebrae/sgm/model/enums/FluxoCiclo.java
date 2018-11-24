package br.com.sebrae.sgm.model.enums;

import br.com.sebrae.sgm.model.fluxo.Fluxo;
import br.com.sebrae.sgm.model.fluxo.Fluxo1;

public enum FluxoCiclo {

	//desempenho
	FX1("Colaborador insere meta, Gerente valida e Comit\u00EA avalia", new Fluxo1()),
	FX2("Colaborador insere meta, Comit\u00EA avalia e Gerente valida", new Fluxo1()),
	FX3("Gerente insere meta para colaborador, Comit\u00EA avalia e Colaborador da ciencia", new Fluxo1()),
	FX4("Gerente insere meta para colaborador, Colaborador da ciencia e Comite avalia", new Fluxo1()),
	FX5("UGP insere meta para colaborador, Gerente valida, Comite analisa e Colaborador da ciencia", new Fluxo1()),
	FX6("UGP insere meta para colaborador, Comite analisa, Gerente valida meta e Colaborador da ciencia", new Fluxo1()),
	FX7("UGP insere meta para colaborador, Gerente valida meta e Colaborador da ciencia", new Fluxo1()),
	FX8("UGP insere meta para colaborador, Gerente valida meta e Colaborador da ciencia", new Fluxo1()),
	//desenvolvimento
	FX9("Colaborador insere meta e Gerente aprova", new Fluxo1()),
	FX10("Colaborador insere meta, Gerente aprova e Comite analisa metododologia", new Fluxo1()),
	FX11("Colaborador insere meta, Comite analisa metododologia e Gerente aprova", new Fluxo1()),
	;

	private FluxoCiclo(String value, Fluxo fluxo) {
		this.value = value;
		this.fluxo = fluxo;
	}

	final String value;
	final Fluxo fluxo;

	public String getValue() {
		return value;
	}

	public Fluxo getFluxo() {
		return fluxo;
	}

}
