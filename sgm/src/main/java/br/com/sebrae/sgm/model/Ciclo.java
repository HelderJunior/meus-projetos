package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.UF;

@Entity
@Table(name = "CICLO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Ciclo.findAll", query = "SELECT c FROM Ciclo c order by c.vigencia asc"),
		@NamedQuery(name = "Ciclo.findById", query = "SELECT c FROM Ciclo c WHERE c.id = :id"),
		@NamedQuery(name = "Ciclo.findByVigenciaUF", query = "SELECT c FROM Ciclo c WHERE c.vigencia = :vigencia and c.uf = :uf"),
		@NamedQuery(name = "Ciclo.findByDescricao", query = "SELECT c FROM Ciclo c WHERE c.descricao = :descricao"),
		@NamedQuery(name = "Ciclo.findByUf", query = "SELECT c FROM Ciclo c WHERE c.uf = :uf"),
		@NamedQuery(name = "Ciclo.findByUfStatusConfiguracao", query = "SELECT distinct c FROM Ciclo c join c.configuracoes cc WHERE c.uf = :uf and c.status = 'I'"),
		@NamedQuery(name = "Ciclo.findByVigencia", query = "SELECT c FROM Ciclo c WHERE c.vigencia = :vigencia"),
		@NamedQuery(name = "Ciclo.findAllAndamento", query = "SELECT c FROM Ciclo c where c.status <> 'F' order by c.vigencia asc") })
public class Ciclo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "DESCRICAO")
	private String descricao;

	@Basic(optional = false)
	@Column(name = "UF")
	@Enumerated(EnumType.STRING)
	private UF uf;

	@Basic(optional = false)
	@Column(name = "VIGENCIA")
	private Integer vigencia;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private StatusCiclo status = StatusCiclo.N;

	@Column(name = "STATUS_CONFIGURACAO")
	@Enumerated(EnumType.STRING)
	private StatusConfiguracao statusConfiguracao = StatusConfiguracao.A;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "ciclo")
	private List<CicloConfiguracao> configuracoes;

	@Transient
	private String codigo;

	@Transient
	private List<Fase> fasesCiclo;

	public Ciclo() {
	}

	public Ciclo(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public Integer getVigencia() {
		return vigencia;
	}

	public void setVigencia(Integer vigencia) {
		this.vigencia = vigencia;
	}

	public StatusCiclo getStatusPactuacao(TipoConfiguracaoCiclo tipoConfigurcao, TipoGrupo tipoCalendario) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			FaseCicloConfiguracao fcc = cc.getFaseCicloByTipo(tipoCalendario);
			if (fcc != null) {
				FaseCiclo fc = fcc.getFaseByTipo(Fase.P);
				if (fc != null)
					return fc.getStatus();
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusPactuacao(TipoConfiguracaoCiclo tipoConfigurcao) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			List<FaseCicloConfiguracao> fccs = cc.getFasesCicloConfiguracao();
			if (fccs != null) {
				for (FaseCicloConfiguracao fcc : fccs) {
					FaseCiclo fc = fcc.getFaseByTipo(Fase.P);
					if (fc != null)
						return fc.getStatus();
				}
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusRepactuacao(TipoConfiguracaoCiclo tipoConfigurcao, TipoGrupo tipoCalendario) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			FaseCicloConfiguracao fcc = cc.getFaseCicloByTipo(tipoCalendario);
			if (fcc != null) {
				FaseCiclo fc = fcc.getFaseByTipo(Fase.R);
				if (fc != null)
					return fc.getStatus();
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusRepactuacao(TipoConfiguracaoCiclo tipoConfigurcao) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			List<FaseCicloConfiguracao> fccs = cc.getFasesCicloConfiguracao();
			if (fccs != null) {
				for (FaseCicloConfiguracao fcc : fccs) {
					FaseCiclo fc = fcc.getFaseByTipo(Fase.R);
					if (fc != null)
						return fc.getStatus();
				}
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusMonitoramento(TipoConfiguracaoCiclo tipoConfigurcao, TipoGrupo tipoCalendario) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			FaseCicloConfiguracao fcc = cc.getFaseCicloByTipo(tipoCalendario);
			if (fcc != null) {
				FaseCiclo fc = null;
				if (tipoConfigurcao == TipoConfiguracaoCiclo.DESEMP) {
					fc = fcc.getFaseByTipo(Fase.M);
				} else {
					fc = fcc.getFaseByTipo(Fase.J);
				}
				if (fc != null)
					return fc.getStatus();
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusMonitoramento(TipoConfiguracaoCiclo tipoConfigurcao) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			List<FaseCicloConfiguracao> fccs = cc.getFasesCicloConfiguracao();
			if (fccs != null) {
				for (FaseCicloConfiguracao fcc : fccs) {
					FaseCiclo fc = fcc.getFaseByTipo(Fase.M);
					if (fc != null)
						return fc.getStatus();
				}
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusValidacao(TipoConfiguracaoCiclo tipoConfigurcao, TipoGrupo tipoCalendario) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			FaseCicloConfiguracao fcc = cc.getFaseCicloByTipo(tipoCalendario);
			if (fcc != null) {
				FaseCiclo fc = fcc.getFaseByTipo(Fase.V);
				if (fc != null)
					return fc.getStatus();
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusValidacao(TipoConfiguracaoCiclo tipoConfigurcao) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			List<FaseCicloConfiguracao> fccs = cc.getFasesCicloConfiguracao();
			if (fccs != null) {
				for (FaseCicloConfiguracao fcc : fccs) {
					FaseCiclo fc = fcc.getFaseByTipo(Fase.V);
					if (fc != null)
						return fc.getStatus();
				}
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusAuditoria(TipoConfiguracaoCiclo tipoConfigurcao, TipoGrupo tipoCalendario) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			FaseCicloConfiguracao fcc = cc.getFaseCicloByTipo(tipoCalendario);
			if (fcc != null) {
				FaseCiclo fc = fcc.getFaseByTipo(Fase.A);
				if (fc != null)
					return fc.getStatus();
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusAuditoria(TipoConfiguracaoCiclo tipoConfigurcao) {
		CicloConfiguracao cc = this.getConfiguracaoByTipo(tipoConfigurcao);
		if (cc != null) {
			List<FaseCicloConfiguracao> fccs = cc.getFasesCicloConfiguracao();
			if (fccs != null) {
				for (FaseCicloConfiguracao fcc : fccs) {
					FaseCiclo fc = fcc.getFaseByTipo(Fase.A);
					if (fc != null)
						return fc.getStatus();
				}
			}
		}
		return StatusCiclo.N;
	}

	public StatusConfiguracao getStatusConfiguracao() {
		return statusConfiguracao;
	}

	public void setStatusConfiguracao(StatusConfiguracao statusConfiguracao) {
		this.statusConfiguracao = statusConfiguracao;
	}

	public StatusCiclo getStatusCiclo() {
		return this.status;
	}

	public List<Fase> getFasesCiclo(TipoConfiguracaoCiclo tipoConfigurcao, TipoGrupo tipoCalendario) {
		if (fasesCiclo == null) {
			fasesCiclo = new ArrayList<Fase>();
			if (getStatusPactuacao(tipoConfigurcao, tipoCalendario) == StatusCiclo.I) {
				fasesCiclo.add(Fase.P);
			}
			if (getStatusRepactuacao(tipoConfigurcao, tipoCalendario) == StatusCiclo.I) {
				fasesCiclo.add(Fase.R);
			}
			if (getStatusMonitoramento(tipoConfigurcao, tipoCalendario) == StatusCiclo.I) {
				fasesCiclo.add(Fase.M);
			}
			if (getStatusValidacao(tipoConfigurcao, tipoCalendario) == StatusCiclo.I) {
				fasesCiclo.add(Fase.V);
			}
			if (getStatusAuditoria(tipoConfigurcao, tipoCalendario) == StatusCiclo.I) {
				fasesCiclo.add(Fase.A);
			}
		}
		return fasesCiclo;
	}

	public String getCodigo() {
		if (StringUtils.isBlank(this.codigo)) {
			if (this.id != null) {
				codigo = "";
				codigo = codigo.concat(this.getUf().toString());
				codigo = codigo.concat(String.format("%05d", this.getId()));
			}
		}
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public int getNivelFaseAtual(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		return getFasesCiclo(tipoConfiguracao, tipoGrupo).size();
	}

	public void verificaStatusConfiguracaoGlobal() {
		StatusConfiguracao st = StatusConfiguracao.C;
		if (this.configuracoes != null && !this.configuracoes.isEmpty()) {
			for (CicloConfiguracao cc : this.configuracoes) {
				cc.verificaStatusConfiguracaoGlobal();
				if (cc.getStatusConfiguracao() == StatusConfiguracao.A) {
					st = StatusConfiguracao.A;
				} 
				/*
				else {
					this.setStatus(StatusCiclo.I);
				}*/
			}
		} else {
			st = StatusConfiguracao.A;
		}

		setStatusConfiguracao(st);
	}

	public StatusCiclo getStatus() {
		return status;
	}

	public void setStatus(StatusCiclo status) {
		this.status = status;
	}

	public Date getDataInicioPactuacao(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
		if (cc != null) {
			Calendario c = cc.getCalendarioByTipo(tipoGrupo);
			if (c != null) {
				retorno = c.getDtInicioPactuacao();
			}
		}
		return retorno;
	}

	public Date getDataFimPactuacao(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
		if (cc != null) {
			Calendario c = cc.getCalendarioByTipo(tipoGrupo);
			if (c != null) {
				retorno = c.getDtFimPactuacao();
			}
		}
		return retorno;
	}

	public Date getDataInicioRepactuacao(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
		if (cc != null) {
			Calendario c = cc.getCalendarioByTipo(tipoGrupo);
			if (c != null) {
				retorno = c.getDtInicioRepactuacao();
			}
		}
		return retorno;
	}

	public Date getDataFimRepactuacao(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
		if (cc != null) {
			Calendario c = cc.getCalendarioByTipo(tipoGrupo);
			if (c != null) {
				retorno = c.getDtFimRepactuacao();
			}
		}
		return retorno;
	}

	public Date getDataInicioMonitoramentoAjustes(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
		if (cc != null) {
			Calendario c = cc.getCalendarioByTipo(tipoGrupo);
			if (c != null) {
				if (tipoConfiguracao == TipoConfiguracaoCiclo.DESEMP)
					retorno = c.getDtInicioMonitoramento();
				if (tipoConfiguracao == TipoConfiguracaoCiclo.DESENV)
					retorno = c.getDtInicioAjustes();
			}
		}
		return retorno;
	}

	public Date getDataFimMonitoramentoAjustes(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
		if (cc != null) {
			Calendario c = cc.getCalendarioByTipo(tipoGrupo);
			if (c != null) {
				if (tipoConfiguracao == TipoConfiguracaoCiclo.DESEMP)
					retorno = c.getDtFimMonitoramento();
				if (tipoConfiguracao == TipoConfiguracaoCiclo.DESENV)
					retorno = c.getDtFimAjustes();
			}
		}
		return retorno;
	}

	public Date getDataInicioValidacao(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
		if (cc != null) {
			Calendario c = cc.getCalendarioByTipo(tipoGrupo);
			if (c != null) {
				retorno = c.getDtInicioValidacao();
			}
		}
		return retorno;
	}

	public Date getDataFimValidacao(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
		if (cc != null) {
			Calendario c = cc.getCalendarioByTipo(tipoGrupo);
			if (c != null) {
				retorno = c.getDtFimValidacao();
			}
		}
		return retorno;
	}

	public Date getDataInicioAuditoria(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		if (tipoConfiguracao == TipoConfiguracaoCiclo.DESEMP) {
			CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
			if (cc != null) {
				if (cc.getConfiguracaoAuditoria() != null)
					retorno = cc.getConfiguracaoAuditoria().getDtInicio();
			}
		}
		return retorno;
	}

	public Date getDataFimAuditoria(TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) {
		Date retorno = null;
		if (tipoConfiguracao == TipoConfiguracaoCiclo.DESEMP) {
			CicloConfiguracao cc = getConfiguracaoByTipo(tipoConfiguracao);
			if (cc != null) {
				if (cc.getConfiguracaoAuditoria() != null)
					retorno = cc.getConfiguracaoAuditoria().getDtFim();
			}
		}
		return retorno;
	}

	public CicloConfiguracao getConfiguracaoByTipo(TipoConfiguracaoCiclo tipoConfiguracao) {
		if (this.configuracoes != null && !this.configuracoes.isEmpty()) {
			for (CicloConfiguracao cc : this.configuracoes) {
				if (cc.getTipoConfiguracao() == tipoConfiguracao) {
					return cc;
				}
			}
		}
		return null;
	}

	public List<CicloConfiguracao> getConfiguracoes() {
		return configuracoes;
	}

	public void setConfiguracoes(List<CicloConfiguracao> configuracoes) {
		this.configuracoes = configuracoes;
	}

	public boolean isPodeIniciar() {
		List<CicloConfiguracao> configuracoes = getConfiguracoes();
		if (configuracoes != null && !configuracoes.isEmpty()) {
			for (CicloConfiguracao cicloConfiguracao : configuracoes) {
				if (!cicloConfiguracao.isPodeIniciar()) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Ciclo)) {
			return false;
		}
		Ciclo other = (Ciclo) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Ciclo[ id=" + id + " ]";
	}

	@PreUpdate
	public void prePersist() {
		verificaStatusConfiguracaoGlobal();
	}

}
