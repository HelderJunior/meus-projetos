package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.PeriodicidadeEnvio;

/**
 * @author Diego
 */
@Entity
@Table(name = "CONFIGURACAO_EMAIL")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "ConfiguracaoEmail.findAll", query = "SELECT c FROM ConfiguracaoEmail c"),
		@NamedQuery(name = "ConfiguracaoEmail.findById", query = "SELECT c FROM ConfiguracaoEmail c WHERE c.id = :id") })
public class ConfiguracaoEmail implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = Fase.class)
	@CollectionTable(name = "CONFIGURACAO_EMAIL_FASE", joinColumns = @JoinColumn(name = "ID_CONFIGURACAO_EMAIL"))
	@Column(name = "FASE_CICLO")
	private List<Fase> fasesCiclo;

	@Enumerated(EnumType.ORDINAL)
	@ElementCollection(targetClass = PeriodicidadeEnvio.class)
	@CollectionTable(name = "CONFIGURACAO_EMAIL_PERIODICIDADE", joinColumns = @JoinColumn(name = "ID_CONFIGURACAO_EMAIL"))
	@Column(name = "PERIODICIDADE")
	private List<PeriodicidadeEnvio> periodicidades;

	@JoinTable(name = "CONFIGURACAO_EMAIL_USUARIO", joinColumns = { @JoinColumn(name = "ID_CONFIGURACAO_EMAIL", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "ID", referencedColumnName = "ID") })
	@ManyToMany
	private List<Usuario> usuarios;

	@JoinTable(name = "CONFIGURACAO_EMAIL_UNIDADE", inverseJoinColumns = {
			@JoinColumn(name = "UF", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO", referencedColumnName = "UnidadeCodUnidade") }, joinColumns = { @JoinColumn(name = "ID_CONFIGURACAO_EMAIL", referencedColumnName = "ID") })
	@ManyToMany
	private List<Unidade> unidades;

	@JoinTable(name = "CONFIGURACAO_EMAIL_PERFIL", inverseJoinColumns = { @JoinColumn(name = "CHAVE_PERFIL", referencedColumnName = "CHAVE") }, joinColumns = { @JoinColumn(name = "ID_CONFIGURACAO_EMAIL", referencedColumnName = "ID") })
	@ManyToMany
	private List<Perfil> perfis;

	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	public ConfiguracaoEmail() {
	}

	public ConfiguracaoEmail(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Fase> getFasesCiclo() {
		return fasesCiclo;
	}

	public void setFasesCiclo(List<Fase> fasesCiclo) {
		this.fasesCiclo = fasesCiclo;
	}

	public List<PeriodicidadeEnvio> getPeriodicidades() {
		return periodicidades;
	}

	public void setPeriodicidades(List<PeriodicidadeEnvio> periodicidades) {
		this.periodicidades = periodicidades;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Unidade> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public List<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ConfiguracaoEmail)) {
			return false;
		}
		ConfiguracaoEmail other = (ConfiguracaoEmail) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ConfiguracaoEmail[ id=" + id + " ]";
	}

	public ConfiguracaoEmail clone() {
		ConfiguracaoEmail retorno = new ConfiguracaoEmail();
		retorno.id = this.id;
		retorno.cicloConfiguracao = this.cicloConfiguracao;
		retorno.perfis = new ArrayList<Perfil>(getPerfis());
		retorno.usuarios = new ArrayList<Usuario>(getUsuarios());
		retorno.unidades = new ArrayList<Unidade>(getUnidades());
		retorno.fasesCiclo = new ArrayList<Fase>(this.fasesCiclo);
		retorno.periodicidades = new ArrayList<PeriodicidadeEnvio>(this.periodicidades);
		return retorno;
	}

}
