package br.com.sebrae.sgm.controller.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.SolucaoEducacionalMeta;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;

public class GerenciarMetaDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Usuario usuario;
	private Unidade unidade;

	private Integer qtdMetasAprovadas = 0;
	private Boolean selecaoAprovadas = Boolean.FALSE;
	private List<Meta> metasAprovadas = new ArrayList<Meta>();

	private Integer qtdSolucoesAprovadas = 0;
	private Boolean selecaoSolucaoAprovadas = Boolean.FALSE;
	private List<SolucaoEducacionalMeta> solucoesAprovadas = new ArrayList<SolucaoEducacionalMeta>();

	private Integer qtdSolucoesPendenteAprovacao = 0;
	private Boolean selecaoSolucaoPendenteAprovacao = Boolean.FALSE;
	private List<SolucaoEducacionalMeta> solucoesPendenteAprovacao = new ArrayList<SolucaoEducacionalMeta>();

	private Integer qtdSolucoesRealizadas = 0;
	private Boolean selecaoSolucaoRealizadas = Boolean.FALSE;
	private List<SolucaoEducacionalMeta> solucoesRealizadas = new ArrayList<SolucaoEducacionalMeta>();

	private Integer qtdMetasGravadas = 0;
	private Boolean selecaoGravadas = Boolean.FALSE;
	private List<Meta> metasGravadas = new ArrayList<Meta>();

	private Integer qtdMetasCanceladas = 0;
	private Boolean selecaoCanceladas = Boolean.FALSE;
	private List<Meta> metasCanceladas = new ArrayList<Meta>();

	private Integer qtdPendenteAprovacao = 0;
	private Boolean selecaoPendenteAprovacao = Boolean.FALSE;
	private List<Meta> metasPendenteAprovacao = new ArrayList<Meta>();

	private Integer qtdPendenteCancelamento = 0;
	private Boolean selecaoPendenteCancelamento = Boolean.FALSE;
	private List<Meta> metasPendenteCancelamento = new ArrayList<Meta>();

	private Integer qtdObservacaoSuperior = 0;
	private Boolean selecaoObservacaoSuperior = Boolean.FALSE;
	private List<Meta> metasObservacaoSuperior = new ArrayList<Meta>();

	private Integer qtdObservacaoComite = 0;
	private Boolean selecaoObservacaoComite = Boolean.FALSE;
	private List<Meta> metasObservacaoComite = new ArrayList<Meta>();

	public GerenciarMetaDTO() {
	}

	public GerenciarMetaDTO(Usuario usuario, Integer qtdMetasAprovadas) {
		super();
		this.usuario = usuario;
		this.qtdMetasAprovadas = qtdMetasAprovadas;
	}

	public GerenciarMetaDTO(Unidade unidade, Integer qtdMetasAprovadas) {
		super();
		this.unidade = unidade;
		this.qtdMetasAprovadas = qtdMetasAprovadas;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Integer getQtdMetasAprovadas() {
		return qtdMetasAprovadas;
	}

	public void setQtdMetasAprovadas(Integer qtdMetasAprovadas) {
		this.qtdMetasAprovadas = qtdMetasAprovadas;
	}

	public Integer getQtdMetasCanceladas() {
		return qtdMetasCanceladas;
	}

	public void setQtdMetasCanceladas(Integer qtdMetasCanceladas) {
		this.qtdMetasCanceladas = qtdMetasCanceladas;
	}

	public Integer getQtdObservacaoComite() {
		return qtdObservacaoComite;
	}

	public void setQtdObservacaoComite(Integer qtdObservacaoComite) {
		this.qtdObservacaoComite = qtdObservacaoComite;
	}

	public Integer getQtdObservacaoSuperior() {
		return qtdObservacaoSuperior;
	}

	public void setQtdObservacaoSuperior(Integer qtdObservacaoSuperior) {
		this.qtdObservacaoSuperior = qtdObservacaoSuperior;
	}

	public Integer getQtdPendenteAprovacao() {
		return qtdPendenteAprovacao;
	}

	public void setQtdPendenteAprovacao(Integer qtdPendenteAprovacao) {
		this.qtdPendenteAprovacao = qtdPendenteAprovacao;
	}

	public Integer getQtdPendenteCancelamento() {
		return qtdPendenteCancelamento;
	}

	public void setQtdPendenteCancelamento(Integer qtdPendenteCancelamento) {
		this.qtdPendenteCancelamento = qtdPendenteCancelamento;
	}

	public Boolean getSelecaoAprovadas() {
		return selecaoAprovadas;
	}

	public void setSelecaoAprovadas(Boolean selecaoAprovadas) {
		this.selecaoAprovadas = selecaoAprovadas;
	}

	public Boolean getSelecaoCanceladas() {
		return selecaoCanceladas;
	}

	public void setSelecaoCanceladas(Boolean selecaoCanceladas) {
		this.selecaoCanceladas = selecaoCanceladas;
	}

	public Boolean getSelecaoObservacaoComite() {
		return selecaoObservacaoComite;
	}

	public void setSelecaoObservacaoComite(Boolean selecaoObservacaoComite) {
		this.selecaoObservacaoComite = selecaoObservacaoComite;
	}

	public Boolean getSelecaoObservacaoSuperior() {
		return selecaoObservacaoSuperior;
	}

	public void setSelecaoObservacaoSuperior(Boolean selecaoObservacaoSuperior) {
		this.selecaoObservacaoSuperior = selecaoObservacaoSuperior;
	}

	public Boolean getSelecaoPendenteAprovacao() {
		return selecaoPendenteAprovacao;
	}

	public void setSelecaoPendenteAprovacao(Boolean selecaoPendenteAprovacao) {
		this.selecaoPendenteAprovacao = selecaoPendenteAprovacao;
	}

	public Boolean getSelecaoPendenteCancelamento() {
		return selecaoPendenteCancelamento;
	}

	public void setSelecaoPendenteCancelamento(Boolean selecaoPendenteCancelamento) {
		this.selecaoPendenteCancelamento = selecaoPendenteCancelamento;
	}

	public List<Meta> getMetasAprovadas() {
		return metasAprovadas;
	}

	public void setMetasAprovadas(List<Meta> metasAprovadas) {
		if (metasAprovadas != null) {
			setQtdMetasAprovadas(metasAprovadas.size());
		}
		this.metasAprovadas = metasAprovadas;
	}

	public List<Meta> getMetasCanceladas() {
		return metasCanceladas;
	}

	public void setMetasCanceladas(List<Meta> metasCanceladas) {
		if (metasCanceladas != null) {
			setQtdMetasCanceladas(metasCanceladas.size());
		}
		this.metasCanceladas = metasCanceladas;
	}

	public List<Meta> getMetasObservacaoComite() {
		return metasObservacaoComite;
	}

	public void setMetasObservacaoComite(List<Meta> metasObservacaoComite) {
		if (metasObservacaoComite != null) {
			setQtdObservacaoComite(metasObservacaoComite.size());
		}
		this.metasObservacaoComite = metasObservacaoComite;
	}

	public List<Meta> getMetasObservacaoSuperior() {
		return metasObservacaoSuperior;
	}

	public void setMetasObservacaoSuperior(List<Meta> metasObservacaoSuperior) {
		if (metasObservacaoSuperior != null) {
			setQtdObservacaoSuperior(metasObservacaoSuperior.size());
		}
		this.metasObservacaoSuperior = metasObservacaoSuperior;
	}

	public List<Meta> getMetasPendenteAprovacao() {
		return metasPendenteAprovacao;
	}

	public void setMetasPendenteAprovacao(List<Meta> metasPendenteAprovacao) {
		if (metasPendenteAprovacao != null) {
			setQtdPendenteAprovacao(metasPendenteAprovacao.size());
		}
		this.metasPendenteAprovacao = metasPendenteAprovacao;
	}

	public List<Meta> getMetasPendenteCancelamento() {
		return metasPendenteCancelamento;
	}

	public void setMetasPendenteCancelamento(List<Meta> metasPendenteCancelamento) {
		if (metasPendenteCancelamento != null) {
			setQtdPendenteCancelamento(metasPendenteCancelamento.size());
		}
		this.metasPendenteCancelamento = metasPendenteCancelamento;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Integer getQtdMetasGravadas() {
		return qtdMetasGravadas;
	}

	public void setQtdMetasGravadas(Integer qtdMetasGravadas) {
		this.qtdMetasGravadas = qtdMetasGravadas;
	}

	public Boolean getSelecaoGravadas() {
		return selecaoGravadas;
	}

	public void setSelecaoGravadas(Boolean selecaoGravadas) {
		this.selecaoGravadas = selecaoGravadas;
	}

	public List<Meta> getMetasGravadas() {
		return metasGravadas;
	}

	public void setMetasGravadas(List<Meta> metasGravadas) {
		if (metasGravadas != null) {
			this.qtdMetasGravadas = metasGravadas.size();
		}
		this.metasGravadas = metasGravadas;
	}

	public Integer getQtdSolucoesAprovadas() {
		return qtdSolucoesAprovadas;
	}

	public void setQtdSolucoesAprovadas(Integer qtdSolucoesAprovadas) {
		this.qtdSolucoesAprovadas = qtdSolucoesAprovadas;
	}

	public Boolean getSelecaoSolucaoAprovadas() {
		return selecaoSolucaoAprovadas;
	}

	public void setSelecaoSolucaoAprovadas(Boolean selecaoSolucaoAprovadas) {
		this.selecaoSolucaoAprovadas = selecaoSolucaoAprovadas;
	}

	public List<SolucaoEducacionalMeta> getSolucoesAprovadas() {
		return solucoesAprovadas;
	}

	public void setSolucoesAprovadas(List<SolucaoEducacionalMeta> solucoesAprovadas) {
		if (solucoesAprovadas != null) {
			this.qtdSolucoesAprovadas = solucoesAprovadas.size();
		}
		this.solucoesAprovadas = solucoesAprovadas;
	}

	public Integer getQtdSolucoesPendenteAprovacao() {
		return qtdSolucoesPendenteAprovacao;
	}

	public void setQtdSolucoesPendenteAprovacao(Integer qtdSolucoesPendenteAprovacao) {
		this.qtdSolucoesPendenteAprovacao = qtdSolucoesPendenteAprovacao;
	}

	public Boolean getSelecaoSolucaoPendenteAprovacao() {
		return selecaoSolucaoPendenteAprovacao;
	}

	public void setSelecaoSolucaoPendenteAprovacao(Boolean selecaoSolucaoPendenteAprovacao) {
		this.selecaoSolucaoPendenteAprovacao = selecaoSolucaoPendenteAprovacao;
	}

	public List<SolucaoEducacionalMeta> getSolucoesPendenteAprovacao() {
		return solucoesPendenteAprovacao;
	}

	public void setSolucoesPendenteAprovacao(List<SolucaoEducacionalMeta> solucoesPendenteAprovacao) {
		if (solucoesPendenteAprovacao != null) {
			this.qtdSolucoesPendenteAprovacao = solucoesPendenteAprovacao.size();
		}
		this.solucoesPendenteAprovacao = solucoesPendenteAprovacao;
	}

	public Integer getQtdSolucoesRealizadas() {
		return qtdSolucoesRealizadas;
	}

	public void setQtdSolucoesRealizadas(Integer qtdSolucoesRealizadas) {
		this.qtdSolucoesRealizadas = qtdSolucoesRealizadas;
	}

	public Boolean getSelecaoSolucaoRealizadas() {
		return selecaoSolucaoRealizadas;
	}

	public void setSelecaoSolucaoRealizadas(Boolean selecaoSolucaoRealizadas) {
		this.selecaoSolucaoRealizadas = selecaoSolucaoRealizadas;
	}

	public List<SolucaoEducacionalMeta> getSolucoesRealizadas() {
		return solucoesRealizadas;
	}

	public void setSolucoesRealizadas(List<SolucaoEducacionalMeta> solucoesRealizadas) {
		if (solucoesRealizadas != null) {
			this.qtdSolucoesRealizadas = solucoesRealizadas.size();
		}
		this.solucoesRealizadas = solucoesRealizadas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GerenciarMetaDTO other = (GerenciarMetaDTO) obj;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}

}
