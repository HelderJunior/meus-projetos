/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sebrae.sgm.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "USUARIO_COMITE_UNIDADE")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "UsuarioComiteUnidade.findAll", query = "SELECT u FROM UsuarioComiteUnidade u"),
		@NamedQuery(name = "UsuarioComiteUnidade.findById", query = "SELECT u FROM UsuarioComiteUnidade u WHERE u.id = :id") })
public class UsuarioComiteUnidade implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@JoinColumn(name = "ID_CONFIGURACAO_COMITE", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private ConfiguracaoComite configuracaoComite;

	@JoinColumns({ @JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO_UNIDADE", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToOne(optional = false)
	private Unidade unidade;

	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Usuario usuario;

	public UsuarioComiteUnidade() {
	}

	public UsuarioComiteUnidade(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
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
		if (!(object instanceof UsuarioComiteUnidade)) {
			return false;
		}
		UsuarioComiteUnidade other = (UsuarioComiteUnidade) object;
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
