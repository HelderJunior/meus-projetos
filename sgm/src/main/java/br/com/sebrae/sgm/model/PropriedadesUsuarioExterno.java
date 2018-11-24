package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.UF;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "PROPRIEDADES_USUARIO_EXTERNO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "PropriedadesUsuarioExterno.findAll", query = "SELECT p FROM PropriedadesUsuarioExterno p"),
		@NamedQuery(name = "PropriedadesUsuarioExterno.findById", query = "SELECT p FROM PropriedadesUsuarioExterno p WHERE p.id = :id"),
		@NamedQuery(name = "PropriedadesUsuarioExterno.findByDtInicio", query = "SELECT p FROM PropriedadesUsuarioExterno p WHERE p.dtInicio = :dtInicio"),
		@NamedQuery(name = "PropriedadesUsuarioExterno.findByDtFim", query = "SELECT p FROM PropriedadesUsuarioExterno p WHERE p.dtFim = :dtFim") })
public class PropriedadesUsuarioExterno implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "DT_INICIO")
	private Date dtInicio;

	@Column(name = "DT_FIM")
	private Date dtFim;

	@JoinColumn(name = "CHAVE_PERFIL", referencedColumnName = "CHAVE")
	@ManyToOne(optional = false)
	private Perfil perfil;

	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = UF.class)
	@CollectionTable(name = "USUARIO_EXTERNO_UF", joinColumns = @JoinColumn(name = "ID_USUARIO_EXTERNO"))
	@Column(name = "UF")
	@OrderBy(value = "UF asc")
	private Set<UF> ufs;

	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Usuario usuario;

	public PropriedadesUsuarioExterno() {
	}

	public PropriedadesUsuarioExterno(Integer id) {
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

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Set<UF> getUfs() {
		return ufs;
	}

	public void setUfs(Set<UF> ufs) {
		this.ufs = ufs;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (getId() != null ? getId().hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof PropriedadesUsuarioExterno)) {
			return false;
		}
		PropriedadesUsuarioExterno other = (PropriedadesUsuarioExterno) object;
		if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.PropriedadesUsuarioExterno[ id=" + getId() + " ]";
	}

}
