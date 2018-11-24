package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;

import br.com.sebrae.sgm.model.enums.TipoGrupo;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "CALENDARIO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Calendario.findAll", query = "SELECT c FROM Calendario c"),
		@NamedQuery(name = "Calendario.findById", query = "SELECT c FROM Calendario c WHERE c.id = :id"),
		@NamedQuery(name = "Calendario.findByDtInicioPactuacao", query = "SELECT c FROM Calendario c WHERE c.dtInicioPactuacao = :dtInicioPactuacao"),
		@NamedQuery(name = "Calendario.findByDtFimPactuacao", query = "SELECT c FROM Calendario c WHERE c.dtFimPactuacao = :dtFimPactuacao"),
		@NamedQuery(name = "Calendario.findByDtInicioRepactuacao", query = "SELECT c FROM Calendario c WHERE c.dtInicioRepactuacao = :dtInicioRepactuacao"),
		@NamedQuery(name = "Calendario.findByDtFimRepactuacao", query = "SELECT c FROM Calendario c WHERE c.dtFimRepactuacao = :dtFimRepactuacao"),
		@NamedQuery(name = "Calendario.findByDtInicioMonitoramento", query = "SELECT c FROM Calendario c WHERE c.dtInicioMonitoramento = :dtInicioMonitoramento"),
		@NamedQuery(name = "Calendario.findByDtFimMonitoramento", query = "SELECT c FROM Calendario c WHERE c.dtFimMonitoramento = :dtFimMonitoramento"),
		@NamedQuery(name = "Calendario.findByDtInicioValidacao", query = "SELECT c FROM Calendario c WHERE c.dtInicioValidacao = :dtInicioValidacao"),
		@NamedQuery(name = "Calendario.findByDtFimValidacao", query = "SELECT c FROM Calendario c WHERE c.dtFimValidacao = :dtFimValidacao"),
		@NamedQuery(name = "Calendario.findByTipo", query = "SELECT c FROM Calendario c WHERE c.tipo = :tipo") })
public class Calendario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "DT_INICIO_PACTUACAO")
	private Date dtInicioPactuacao;

	@Column(name = "DT_FIM_PACTUACAO")
	private Date dtFimPactuacao;

	@Column(name = "DT_INICIO_REPACTUACAO")
	private Date dtInicioRepactuacao;

	@Column(name = "DT_FIM_REPACTUACAO")
	private Date dtFimRepactuacao;

	@Column(name = "DT_INICIO_MONITORAMENTO")
	private Date dtInicioMonitoramento;

	@Column(name = "DT_FIM_MONITORAMENTO")
	private Date dtFimMonitoramento;

	@Column(name = "DT_INICIO_AJUSTES")
	private Date dtInicioAjustes;

	@Column(name = "DT_FIM_AJUSTES")
	private Date dtFimAjustes;

	@Column(name = "DT_INICIO_VALIDACAO")
	private Date dtInicioValidacao;

	@Column(name = "DT_FIM_VALIDACAO")
	private Date dtFimValidacao;

	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO")
	private TipoGrupo tipo;

	@OneToMany(mappedBy = "calendario", cascade = CascadeType.ALL)
	private List<Prorrogacao> prorrogacoes;

	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	public Calendario() {
	}

	public Calendario(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDtInicioPactuacao() {
		return dtInicioPactuacao;
	}

	public void setDtInicioPactuacao(Date dtInicioPactuacao) {
		this.dtInicioPactuacao = dtInicioPactuacao;
	}
	
	public String getDtInicioPactuacaoForDisable() {
		if(dtInicioPactuacao != null){
			return new SimpleDateFormat("d/MM/yyyy").format(dtInicioPactuacao);
		}
		return "";
	}

	public Date getDtFimPactuacao() {
		return dtFimPactuacao;
	}

	public void setDtFimPactuacao(Date dtFimPactuacao) {
		this.dtFimPactuacao = dtFimPactuacao;
	}

	public Date getDtInicioRepactuacao() {
		return dtInicioRepactuacao;
	}

	public void setDtInicioRepactuacao(Date dtInicioRepactuacao) {
		this.dtInicioRepactuacao = dtInicioRepactuacao;
	}

	public Date getDtFimRepactuacao() {
		return dtFimRepactuacao;
	}

	public void setDtFimRepactuacao(Date dtFimRepactuacao) {
		this.dtFimRepactuacao = dtFimRepactuacao;
	}

	public Date getDtInicioMonitoramento() {
		return dtInicioMonitoramento;
	}

	public void setDtInicioMonitoramento(Date dtInicioMonitoramento) {
		this.dtInicioMonitoramento = dtInicioMonitoramento;
	}

	public Date getDtFimMonitoramento() {
		return dtFimMonitoramento;
	}

	public void setDtFimMonitoramento(Date dtFimMonitoramento) {
		this.dtFimMonitoramento = dtFimMonitoramento;
	}

	public Date getDtInicioValidacao() {
		return dtInicioValidacao;
	}

	public void setDtInicioValidacao(Date dtInicioValidacao) {
		this.dtInicioValidacao = dtInicioValidacao;
	}

	public Date getDtFimValidacao() {
		return dtFimValidacao;
	}

	public void setDtFimValidacao(Date dtFimValidacao) {
		this.dtFimValidacao = dtFimValidacao;
	}

	public TipoGrupo getTipo() {
		return tipo;
	}

	public void setTipo(TipoGrupo tipo) {
		this.tipo = tipo;
	}

	@XmlTransient
	public List<Prorrogacao> getProrrogacoes() {
		return prorrogacoes;
	}

	public void setProrrogacoes(List<Prorrogacao> prorrogacoes) {
		this.prorrogacoes = prorrogacoes;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public boolean isCompleto() {
		if (this.getTipo() == TipoGrupo.C) {
			if (this.cicloConfiguracao.getNaoParametrizarMetasDesempenhoIndividual()) {
				return true;
			}
		}
		if (this.getTipo() == TipoGrupo.E) {
			if (this.cicloConfiguracao.getNaoParametrizarMetasDesempenhoEquipe()) {
				return true;
			}
		}
		return (dtInicioPactuacao != null && dtFimPactuacao != null && dtInicioRepactuacao != null
				&& dtFimRepactuacao != null && (dtInicioMonitoramento != null || dtInicioAjustes != null)
				&& (dtFimMonitoramento != null || dtFimAjustes != null) && dtInicioValidacao != null && dtFimValidacao != null);
	}

	public Date getDtInicioAuditoria() {
		if (this.cicloConfiguracao != null && this.cicloConfiguracao.getConfiguracaoAuditoria() != null) {
			return this.cicloConfiguracao.getConfiguracaoAuditoria().getDtInicio();
		}
		return null;
	}
	
	public void setDtInicioAuditoria(Date dataInicioAuditoria){
		if(this.cicloConfiguracao != null && this.cicloConfiguracao.getConfiguracaoAuditoria() == null){
			ConfiguracaoAuditoria confAuditoria = new ConfiguracaoAuditoria();
			confAuditoria.setCicloConfiguracao(this.cicloConfiguracao);
			this.cicloConfiguracao.setConfiguracaoAuditoria(confAuditoria);
		}
		this.cicloConfiguracao.getConfiguracaoAuditoria().setDtInicio(dataInicioAuditoria);
	}

	public Date getDtFimAuditoria() {
		if (this.cicloConfiguracao != null && this.cicloConfiguracao.getConfiguracaoAuditoria() != null) {
			return this.cicloConfiguracao.getConfiguracaoAuditoria().getDtFim();
		}
		return null;
	}
	
	public void setDtFimAuditoria(Date dataFimAuditoria){
		if(this.cicloConfiguracao != null && this.cicloConfiguracao.getConfiguracaoAuditoria() == null){
			ConfiguracaoAuditoria confAuditoria = new ConfiguracaoAuditoria();
			confAuditoria.setCicloConfiguracao(this.cicloConfiguracao);
			this.cicloConfiguracao.setConfiguracaoAuditoria(confAuditoria);
		}
		this.cicloConfiguracao.getConfiguracaoAuditoria().setDtFim(dataFimAuditoria);
	}

	public Date getDtInicioAjustes() {
		return dtInicioAjustes;
	}

	public void setDtInicioAjustes(Date dtInicioAjustes) {
		this.dtInicioAjustes = dtInicioAjustes;
	}

	public Date getDtFimAjustes() {
		return dtFimAjustes;
	}

	public void setDtFimAjustes(Date dtFimAjustes) {
		this.dtFimAjustes = dtFimAjustes;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Calendario)) {
			return false;
		}
		Calendario other = (Calendario) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Calendario[ id=" + id + " ]";
	}

}
