package br.com.sebrae.sgm.controller.dto;

import java.io.Serializable;

import br.com.sebrae.sgm.model.Meta;

public class MetaSelecaoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Meta meta;

	private Boolean selecionado = Boolean.FALSE;

	public MetaSelecaoDTO(Meta meta) {
		super();
		this.meta = meta;
	}

	public MetaSelecaoDTO(Meta meta, Boolean selecionado) {
		super();
		this.meta = meta;
		this.selecionado = selecionado;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

}
