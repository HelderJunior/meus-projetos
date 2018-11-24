package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
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

import br.com.sebrae.sgm.model.enums.TipoParametroAuditoria;
import br.com.sebrae.sgm.model.enums.UF;

/**
 * @author Diego
 */
@Entity
@Table(name = "PARAMETROS_AUDITORIA")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ParametrosAuditoria.findAll", query = "SELECT p FROM ParametrosAuditoria p"),
		@NamedQuery(name = "ParametrosAuditoria.findById", query = "SELECT p FROM ParametrosAuditoria p WHERE p.id = :id"),
		@NamedQuery(name = "ParametrosAuditoria.findByTipoParametro", query = "SELECT p FROM ParametrosAuditoria p WHERE p.tipoParametro = :tipoParametro"),
		@NamedQuery(name = "ParametrosAuditoria.findByUfUsuario", query = "SELECT p FROM ParametrosAuditoria p WHERE p.ufUsuario = :ufUsuario") })
public class ParametrosAuditoria implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_PARAMETRO")
	private TipoParametroAuditoria tipoParametro;

	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	@Column(name = "UF_USUARIO")
	private UF ufUsuario;

	@JoinTable(name = "PARAMETRO_AUDITORIA_UNIDADE", joinColumns = { @JoinColumn(name = "ID", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO_UNIDADE", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToMany
	private List<Unidade> unidades;

	@JoinTable(name = "PARAMETRO_AUDITORIA_USUARIO", 
			joinColumns = { @JoinColumn(name = "ID", referencedColumnName = "ID") }, 
			inverseJoinColumns = { @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID") })
	@ManyToMany
	private List<Usuario> usuarios;

	@JoinColumn(name = "ID_CONFIGURACAO_AUDITORIA", referencedColumnName = "ID")
	@ManyToOne
	private ConfiguracaoAuditoria configuracaoAuditoria;

	@JoinColumn(name = "CHAVE_PERFIL", referencedColumnName = "CHAVE")
	@ManyToOne
	private Perfil perfil;

	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")
	@ManyToOne
	private Usuario usuario;

	public ParametrosAuditoria() {
	}

	public ParametrosAuditoria(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TipoParametroAuditoria getTipoParametro() {
		return tipoParametro;
	}

	public void setTipoParametro(TipoParametroAuditoria tipoParametro) {
		this.tipoParametro = tipoParametro;
	}

	public UF getUfUsuario() {
		return ufUsuario;
	}

	public void setUfUsuario(UF ufUsuario) {
		this.ufUsuario = ufUsuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Unidade> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public ConfiguracaoAuditoria getConfiguracaoAuditoria() {
		return configuracaoAuditoria;
	}

	public void setConfiguracaoAuditoria(ConfiguracaoAuditoria configuracaoAuditoria) {
		this.configuracaoAuditoria = configuracaoAuditoria;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ParametrosAuditoria)) {
			return false;
		}
		ParametrosAuditoria other = (ParametrosAuditoria) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ParametrosAuditoria[ id=" + id + " ]";
	}

}
