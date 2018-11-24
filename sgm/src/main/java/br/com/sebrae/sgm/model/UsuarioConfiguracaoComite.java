package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "USUARIO_CONFIGURACAO_COMITE")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "UsuarioConfiguracaoComite.findAll", query = "SELECT u FROM UsuarioConfiguracaoComite u"),
		@NamedQuery(name = "UsuarioConfiguracaoComite.findById", query = "SELECT u FROM UsuarioConfiguracaoComite u WHERE u.id = :id") })
public class UsuarioConfiguracaoComite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@JoinColumn(name = "ID_CONFIGURACAO_COMITE", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private ConfiguracaoComite configuracaoComite;

	@ManyToMany
	@JoinTable(name = "USUARIO_CONFIGURACAO_COMITE_UNIDADE", inverseJoinColumns = {
			@JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO_UNIDADE", referencedColumnName = "UnidadeCodUnidade") }, joinColumns = { @JoinColumn(name = "ID", referencedColumnName = "ID") })
	private List<Unidade> unidades;

	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Usuario usuario;

	public UsuarioConfiguracaoComite() {
	}

	public UsuarioConfiguracaoComite(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Unidade> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public ConfiguracaoComite getConfiguracaoComite() {
		return configuracaoComite;
	}

	public void setConfiguracaoComite(ConfiguracaoComite configuracaoComite) {
		this.configuracaoComite = configuracaoComite;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof UsuarioConfiguracaoComite)) {
			return false;
		}
		UsuarioConfiguracaoComite other = (UsuarioConfiguracaoComite) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.UsuarioComiteUnidade[ id=" + id + " ]";
	}

}
