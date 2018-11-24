package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "FUNCIONALIDADE")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Funcionalidade.findAll", query = "SELECT f FROM Funcionalidade f"),
		@NamedQuery(name = "Funcionalidade.findByChave", query = "SELECT f FROM Funcionalidade f WHERE f.chave = :chave"),
		@NamedQuery(name = "Funcionalidade.findByDescricao", query = "SELECT f FROM Funcionalidade f WHERE f.descricao = :descricao") })
public class Funcionalidade implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@Column(name = "CHAVE")
	private String chave;
	@Basic(optional = false)
	@Column(name = "DESCRICAO")
	private String descricao;
	@JoinTable(name = "USUARIO_FUNCIONALIDADE", joinColumns = { @JoinColumn(name = "CHAVE_FUNCIONALIDADE", referencedColumnName = "CHAVE") }, inverseJoinColumns = { @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID") })
	@ManyToMany
	private List<Usuario> usuarios;

	public Funcionalidade() {
	}

	public Funcionalidade(String chave) {
		this.chave = chave;
	}

	public Funcionalidade(String chave, String descricao) {
		this.chave = chave;
		this.descricao = descricao;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (chave != null ? chave.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Funcionalidade)) {
			return false;
		}
		Funcionalidade other = (Funcionalidade) object;
		if ((this.chave == null && other.chave != null) || (this.chave != null && !this.chave.equals(other.chave))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Funcionalidade[ chave=" + chave + " ]";
	}

}
