package br.com.sebrae.sgm.model;

import java.io.Serializable;
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

/**
 * @author Diego
 */
@Entity
@Table(name = "CONFIGURACAO_COMITE")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ConfiguracaoComite.findAll", query = "SELECT c FROM ConfiguracaoComite c"),
		@NamedQuery(name = "ConfiguracaoComite.findById", query = "SELECT c FROM ConfiguracaoComite c WHERE c.id = :id") })
public class ConfiguracaoComite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoComite")
	private List<ItemAvaliadoComite> itemsAvaliados;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoComite")
	private List<UsuarioConfiguracaoComite> usuariosComiteUnidade;

	@JoinColumn(name = "ID", referencedColumnName = "ID", insertable = false, updatable = false)
	@OneToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	public ConfiguracaoComite() {
	}

	public ConfiguracaoComite(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<ItemAvaliadoComite> getItemsAvaliados() {
		return itemsAvaliados;
	}

	public void setItemsAvaliados(List<ItemAvaliadoComite> itemsAvaliados) {
		this.itemsAvaliados = itemsAvaliados;
	}

	public List<UsuarioConfiguracaoComite> getUsuariosComiteUnidade() {
		return usuariosComiteUnidade;
	}

	public void setUsuariosComiteUnidade(List<UsuarioConfiguracaoComite> usuariosComiteUnidade) {
		this.usuariosComiteUnidade = usuariosComiteUnidade;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ConfiguracaoComite)) {
			return false;
		}
		ConfiguracaoComite other = (ConfiguracaoComite) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ConfiguracaoComite[ id=" + id + " ]";
	}

}
