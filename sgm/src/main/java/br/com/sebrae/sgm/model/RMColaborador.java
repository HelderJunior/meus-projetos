package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.CodSituacaoColaborador;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "RM_Colaborador")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "RMColaborador.findAll", query = "SELECT r FROM RMColaborador r"),
		@NamedQuery(name = "RMColaborador.findByColaboradorCpf", query = "SELECT r FROM RMColaborador r WHERE r.rmColaboradorPK.colaboradorCpf = :colaboradorCpf order by r.colaboradorCodSituacao asc"),
		@NamedQuery(name = "RMColaborador.findAtivosByColaboradorCpf", query = "SELECT r FROM RMColaborador r WHERE r.rmColaboradorPK.colaboradorCpf = :colaboradorCpf and r.colaboradorCodSituacao = 'A'"),
		@NamedQuery(name = "RMColaborador.findUFsColaborador", query = "SELECT r.rmColaboradorPK.unidadeUf FROM RMColaborador r WHERE r.rmColaboradorPK.colaboradorCpf = :colaboradorCpf and r.colaboradorCodSituacao = 'A'")})
public class RMColaborador implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected RMColaboradorPK rmColaboradorPK;

	@Basic(optional = false)
	@Column(name = "ColaboradorNome")
	private String colaboradorNome;

	@Column(name = "ColaboradorEmail")
	private String colaboradorEmail;

	@Column(name = "ColaboradorDtNascimento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date colaboradorDtNascimento;

	@Basic(optional = false)
	@Column(name = "ColaboradorDataAdmissao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date colaboradorDataAdmissao;

	@Column(name = "ColaboradorDataDemissao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date colaboradorDataDemissao;

	@Basic(optional = false)
	@Column(name = "ColaboradorCodSituacao", columnDefinition = "char")
	@Enumerated(EnumType.STRING)
	private CodSituacaoColaborador colaboradorCodSituacao;

	@Basic(optional = false)
	@Column(name = "ColaboradorDescSituacao")
	private String colaboradorDescSituacao;

	@Basic(optional = false)
	@Column(name = "ColaboradorDataAtualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date colaboradorDataAtualizacao;

	@Basic(optional = false)
	@Column(name = "ColaboradorDominio")
	private String colaboradorDominio;

	@Column(name = "ColaboradorLogin")
	private String colaboradorLogin;

	@Basic(optional = false)
	@Column(name = "ColaboradorConfianca")
	private String colaboradorConfianca;

	@Basic(optional = false)
	@Column(name = "ColaboradorIndassistente")
	private String colaboradorIndassistente;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "rMColaborador", fetch = FetchType.LAZY)
	private List<RMLogLicenca> rmLogLicencaList;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "rmColaborador", fetch = FetchType.LAZY)
	private List<RMLogUnidade> rmLogUnidadeList;

	@JoinColumns({ @JoinColumn(name = "EspacoOcupacionalUF", referencedColumnName = "EspacoOcupacionalUF"),
			@JoinColumn(name = "EspacoOcupacionalCodEspOcup", referencedColumnName = "EspacoOcupacionalCodEspOcup") })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private EspacoOcupacional rmEspacoOcupacional;

	@JoinColumns({
			@JoinColumn(name = "UnidadeUf", referencedColumnName = "UnidadeUf", insertable = false, updatable = false),
			@JoinColumn(name = "UnidadeCodUnidade", referencedColumnName = "UnidadeCodUnidade", insertable = false, updatable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Unidade rmUnidade;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "rmColaborador", fetch = FetchType.LAZY)
	private List<RMLogEspOcup> rmLogEspOcupList;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "rmColaborador", fetch = FetchType.LAZY)
	private List<RMCompetencia> rmCompetenciaList;

	@JoinColumns({
		@JoinColumn(name = "ColaboradorCpf", referencedColumnName = "CPF", insertable = false, updatable = false),
		@JoinColumn(name = "ColaboradorMatricula", referencedColumnName = "MATRICULA", insertable = false, updatable = false)})
	@ManyToOne
	private Usuario usuario;

	public RMColaborador() {
	}

	public RMColaboradorPK getRmColaboradorPK() {
		return rmColaboradorPK;
	}

	public void setRmColaboradorPK(RMColaboradorPK rmColaboradorPK) {
		this.rmColaboradorPK = rmColaboradorPK;
	}

	public List<RMLogLicenca> getRmLogLicencaList() {
		return rmLogLicencaList;
	}

	public void setRmLogLicencaList(List<RMLogLicenca> rmLogLicencaList) {
		this.rmLogLicencaList = rmLogLicencaList;
	}

	public List<RMLogUnidade> getRmLogUnidadeList() {
		return rmLogUnidadeList;
	}

	public void setRmLogUnidadeList(List<RMLogUnidade> rmLogUnidadeList) {
		this.rmLogUnidadeList = rmLogUnidadeList;
	}

	public EspacoOcupacional getRmEspacoOcupacional() {
		return rmEspacoOcupacional;
	}

	public void setRmEspacoOcupacional(EspacoOcupacional rmEspacoOcupacional) {
		this.rmEspacoOcupacional = rmEspacoOcupacional;
	}

	public Unidade getRmUnidade() {
		return rmUnidade;
	}

	public void setRmUnidade(Unidade rmUnidade) {
		this.rmUnidade = rmUnidade;
	}

	public List<RMLogEspOcup> getRmLogEspOcupList() {
		return rmLogEspOcupList;
	}

	public void setRmLogEspOcupList(List<RMLogEspOcup> rmLogEspOcupList) {
		this.rmLogEspOcupList = rmLogEspOcupList;
	}

	public List<RMCompetencia> getRmCompetenciaList() {
		return rmCompetenciaList;
	}

	public void setRmCompetenciaList(List<RMCompetencia> rmCompetenciaList) {
		this.rmCompetenciaList = rmCompetenciaList;
	}

	public String getColaboradorNome() {
		return colaboradorNome;
	}

	public void setColaboradorNome(String colaboradorNome) {
		this.colaboradorNome = colaboradorNome;
	}

	public String getColaboradorEmail() {
		return colaboradorEmail;
	}

	public void setColaboradorEmail(String colaboradorEmail) {
		this.colaboradorEmail = colaboradorEmail;
	}

	public Date getColaboradorDtNascimento() {
		return colaboradorDtNascimento;
	}

	public void setColaboradorDtNascimento(Date colaboradorDtNascimento) {
		this.colaboradorDtNascimento = colaboradorDtNascimento;
	}

	public Date getColaboradorDataAdmissao() {
		return colaboradorDataAdmissao;
	}

	public void setColaboradorDataAdmissao(Date colaboradorDataAdmissao) {
		this.colaboradorDataAdmissao = colaboradorDataAdmissao;
	}

	public Date getColaboradorDataDemissao() {
		return colaboradorDataDemissao;
	}

	public void setColaboradorDataDemissao(Date colaboradorDataDemissao) {
		this.colaboradorDataDemissao = colaboradorDataDemissao;
	}

	public CodSituacaoColaborador getColaboradorCodSituacao() {
		return colaboradorCodSituacao;
	}

	public void setColaboradorCodSituacao(CodSituacaoColaborador colaboradorCodSituacao) {
		this.colaboradorCodSituacao = colaboradorCodSituacao;
	}

	public String getColaboradorDescSituacao() {
		return colaboradorDescSituacao;
	}

	public void setColaboradorDescSituacao(String colaboradorDescSituacao) {
		this.colaboradorDescSituacao = colaboradorDescSituacao;
	}

	public Date getColaboradorDataAtualizacao() {
		return colaboradorDataAtualizacao;
	}

	public void setColaboradorDataAtualizacao(Date colaboradorDataAtualizacao) {
		this.colaboradorDataAtualizacao = colaboradorDataAtualizacao;
	}

	public String getColaboradorDominio() {
		return colaboradorDominio;
	}

	public void setColaboradorDominio(String colaboradorDominio) {
		this.colaboradorDominio = colaboradorDominio;
	}

	public String getColaboradorLogin() {
		return colaboradorLogin;
	}

	public void setColaboradorLogin(String colaboradorLogin) {
		this.colaboradorLogin = colaboradorLogin;
	}

	public String getColaboradorConfianca() {
		return colaboradorConfianca;
	}

	public void setColaboradorConfianca(String colaboradorConfianca) {
		this.colaboradorConfianca = colaboradorConfianca;
	}

	public String getColaboradorIndassistente() {
		return colaboradorIndassistente;
	}

	public void setColaboradorIndassistente(String colaboradorIndassistente) {
		this.colaboradorIndassistente = colaboradorIndassistente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isAfastadoLicensa() {
		return (this.getColaboradorCodSituacao() == CodSituacaoColaborador.P
				|| this.getColaboradorCodSituacao() == CodSituacaoColaborador.T
				|| this.getColaboradorCodSituacao() == CodSituacaoColaborador.E
				|| this.getColaboradorCodSituacao() == CodSituacaoColaborador.L || this.getColaboradorCodSituacao() == CodSituacaoColaborador.W);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getRmColaboradorPK() == null) ? 0 : getRmColaboradorPK().hashCode());
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
		RMColaborador other = (RMColaborador) obj;
		if (getRmColaboradorPK() == null) {
			if (other.getRmColaboradorPK() != null)
				return false;
		} else if (!getRmColaboradorPK().equals(other.getRmColaboradorPK()))
			return false;
		return true;
	}

}
