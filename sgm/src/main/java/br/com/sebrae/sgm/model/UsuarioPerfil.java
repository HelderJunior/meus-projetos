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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.TipoMetaAcessoAdministrador;
import br.com.sebrae.sgm.model.enums.UF;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "USUARIO_PERFIL")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "UsuarioPerfil.findAll", query = "SELECT u FROM UsuarioPerfil u"),
		@NamedQuery(name = "UsuarioPerfil.findById", query = "SELECT u FROM UsuarioPerfil u WHERE u.id = :id") })
public class UsuarioPerfil implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@JoinColumn(name = "CHAVE_PERFIL", referencedColumnName = "CHAVE")
	@ManyToOne(optional = false)
	private Perfil perfil;

	@JoinColumns({ @JoinColumn(name = "UF", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToOne
	private Unidade unidade;

	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Usuario usuario;

	@Column(name = "TIPO_META_ACESSO")
	@Enumerated(EnumType.STRING)
	private TipoMetaAcessoAdministrador tipoMetaAcessoAdministrador;

	@Column(name = "UF_SEM_UNIDADE")
	@Enumerated(EnumType.STRING)
	private UF uf;
	
	@Transient
	private Boolean selecionado = Boolean.FALSE;

	public UsuarioPerfil() {
	}

	public UsuarioPerfil(Integer id) {
		this.id = id;
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public TipoMetaAcessoAdministrador getTipoMetaAcessoAdministrador() {
		return tipoMetaAcessoAdministrador;
	}

	public void setTipoMetaAcessoAdministrador(TipoMetaAcessoAdministrador tipoMetaAcessoAdministrador) {
		this.tipoMetaAcessoAdministrador = tipoMetaAcessoAdministrador;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof UsuarioPerfil)) {
			return false;
		}
		UsuarioPerfil other = (UsuarioPerfil) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.UsuarioPerfil[ id=" + id + " ]";
	}
	
	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

}
