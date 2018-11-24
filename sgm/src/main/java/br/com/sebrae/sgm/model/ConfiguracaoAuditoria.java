package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.TipoParametroAuditoria;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * @author Diego
 */
@Entity
@Table(name = "CONFIGURACAO_AUDITORIA")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ConfiguracaoAuditoria.findAll", query = "SELECT c FROM ConfiguracaoAuditoria c"),
		@NamedQuery(name = "ConfiguracaoAuditoria.findById", query = "SELECT c FROM ConfiguracaoAuditoria c WHERE c.id = :id"),
		@NamedQuery(name = "ConfiguracaoAuditoria.findByDtInicio", query = "SELECT c FROM ConfiguracaoAuditoria c WHERE c.dtInicio = :dtInicio"),
		@NamedQuery(name = "ConfiguracaoAuditoria.findByDtFim", query = "SELECT c FROM ConfiguracaoAuditoria c WHERE c.dtFim = :dtFim") })
public class ConfiguracaoAuditoria implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "DT_INICIO")
	private Date dtInicio;

	@Column(name = "DT_FIM")
	private Date dtFim;

	@Column(name = "OBSERVACAO")
	private String observacao;

	@JoinColumn(name = "ID", referencedColumnName = "ID", insertable = false, updatable = false)
	@OneToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	@OneToMany(mappedBy = "configuracaoAuditoria", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Anexo> anexos;

	@OneToMany(mappedBy = "configuracaoAuditoria", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ParametrosAuditoria> parametrosAuditoria;

	public ConfiguracaoAuditoria() {
	}

	public ConfiguracaoAuditoria(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public List<Anexo> getAnexos() {
		return anexos;
	}

	public void setAnexos(List<Anexo> anexos) {
		this.anexos = anexos;
	}

	public List<ParametrosAuditoria> getParametrosAuditoria() {
		return parametrosAuditoria;
	}

	public void setParametrosAuditoria(List<ParametrosAuditoria> parametrosAuditoria) {
		this.parametrosAuditoria = parametrosAuditoria;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
		this.id = cicloConfiguracao.getId();
	}

	public List<ParametrosAuditoria> getParametrosAuditoriaEquipe() {
		List<ParametrosAuditoria> retorno = new ArrayList<ParametrosAuditoria>();
		if (getParametrosAuditoria() != null && !getParametrosAuditoria().isEmpty()) {
			retorno = Lists.newArrayList(Collections2.filter(parametrosAuditoria, new Predicate<ParametrosAuditoria>() {
				@Override
				public boolean apply(ParametrosAuditoria obs) {
					return obs.getTipoParametro() == TipoParametroAuditoria.E;
				}
			}));
		}
		return retorno;
	}

	public List<ParametrosAuditoria> getParametrosAuditoriaColaborador() {
		List<ParametrosAuditoria> retorno = new ArrayList<ParametrosAuditoria>();
		if (getParametrosAuditoria() != null && !getParametrosAuditoria().isEmpty()) {
			retorno = Lists.newArrayList(Collections2.filter(parametrosAuditoria, new Predicate<ParametrosAuditoria>() {
				@Override
				public boolean apply(ParametrosAuditoria obs) {
					return obs.getTipoParametro() == TipoParametroAuditoria.C;
				}
			}));
		}
		return retorno;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ConfiguracaoAuditoria)) {
			return false;
		}
		ConfiguracaoAuditoria other = (ConfiguracaoAuditoria) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ConfiguracaoAuditoria[ id=" + id + " ]";
	}

}
