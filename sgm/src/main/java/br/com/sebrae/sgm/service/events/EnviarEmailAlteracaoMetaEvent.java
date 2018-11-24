package br.com.sebrae.sgm.service.events;

import java.io.Serializable;

import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;

public class EnviarEmailAlteracaoMetaEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private String alteracaoRealizada;
	private Meta meta;
	private TipoConfiguracaoCiclo tipoConfiguracao;
	private TipoGrupo tipoCalendario;

	public EnviarEmailAlteracaoMetaEvent(String alteracaoRealizada, Meta meta, TipoConfiguracaoCiclo tipoConfiguracao,
			TipoGrupo tipoCalendario) {
		super();
		this.alteracaoRealizada = alteracaoRealizada;
		this.meta = meta;
		this.tipoConfiguracao = tipoConfiguracao;
		this.tipoCalendario = tipoCalendario;
	}

	public String getAlteracaoRealizada() {
		return alteracaoRealizada;
	}

	public void setAlteracaoRealizada(String alteracaoRealizada) {
		this.alteracaoRealizada = alteracaoRealizada;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public TipoConfiguracaoCiclo getTipoConfiguracao() {
		return tipoConfiguracao;
	}

	public void setTipoConfiguracao(TipoConfiguracaoCiclo tipoConfiguracao) {
		this.tipoConfiguracao = tipoConfiguracao;
	}

	public TipoGrupo getTipoCalendario() {
		return tipoCalendario;
	}

	public void setTipoCalendario(TipoGrupo tipoCalendario) {
		this.tipoCalendario = tipoCalendario;
	}

}
