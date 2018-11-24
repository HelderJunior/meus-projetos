package br.com.sebrae.sgm.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.TipoAcaoAvaliador;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "AVALIADOR_CICLO")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Avaliador.findAll", query = "SELECT a FROM AvaliadorCiclo a"),
		@NamedQuery(name = "Avaliador.findById", query = "SELECT a FROM AvaliadorCiclo a WHERE a.id = :id") })
public class AvaliadorCiclo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	@JoinColumn(name = "CHAVE_PERFIL", referencedColumnName = "CHAVE")
	@ManyToOne(optional = false)
	private Perfil perfil;

	@JoinColumns({ @JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf", columnDefinition="char"),
			@JoinColumn(name = "CODIGO_UNIDADE", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToOne(optional = false)
	private Unidade unidade;

	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")
	@ManyToOne
	private Usuario usuario;

	@Column(name = "FASE_PACTUACAO")
	private Boolean fasePactuacao = Boolean.FALSE;

	@Column(name = "FASE_REPACTUACAO")
	private Boolean faseRepactuacao = Boolean.FALSE;

	@Column(name = "FASE_AJUSTES")
	private Boolean faseAjustes = Boolean.FALSE;

	@Column(name = "FASE_VALIDACAO")
	private Boolean faseValidacao = Boolean.FALSE;

	@Column(name = "TIPO_ACAO", columnDefinition = "char")
	@Enumerated(EnumType.STRING)
	private TipoAcaoAvaliador tipoAcao;

	public AvaliadorCiclo() {
	}

	public AvaliadorCiclo(Integer id) {
		this.id = id;
	}

	public Boolean getTodasFases() {
		if (fasePactuacao && faseRepactuacao && faseAjustes && faseValidacao) {
			return true;
		}
		return false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Boolean getFasePactuacao() {
		return fasePactuacao;
	}

	public void setFasePactuacao(Boolean fasePactuacao) {
		this.fasePactuacao = fasePactuacao;
	}

	public Boolean getFaseRepactuacao() {
		return faseRepactuacao;
	}

	public void setFaseRepactuacao(Boolean faseRepactuacao) {
		this.faseRepactuacao = faseRepactuacao;
	}

	public Boolean getFaseAjustes() {
		return faseAjustes;
	}

	public void setFaseAjustes(Boolean faseAjustes) {
		this.faseAjustes = faseAjustes;
	}

	public Boolean getFaseValidacao() {
		return faseValidacao;
	}

	public void setFaseValidacao(Boolean faseValidacao) {
		this.faseValidacao = faseValidacao;
	}

	public TipoAcaoAvaliador getTipoAcao() {
		return tipoAcao;
	}

	public void setTipoAcao(TipoAcaoAvaliador tipoAcao) {
		this.tipoAcao = tipoAcao;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AvaliadorCiclo)) {
			return false;
		}
		AvaliadorCiclo other = (AvaliadorCiclo) object;
		if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Avaliador[ id=" + id + " ]";
	}

}
