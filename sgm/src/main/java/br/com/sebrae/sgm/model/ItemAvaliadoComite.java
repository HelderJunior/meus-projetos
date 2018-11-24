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
@Table(name = "ITEM_AVALIADO_COMITE")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ItemAvaliadoComite.findAll", query = "SELECT i FROM ItemAvaliadoComite i"),
		@NamedQuery(name = "ItemAvaliadoComite.findById", query = "SELECT i FROM ItemAvaliadoComite i WHERE i.id = :id"),
		@NamedQuery(name = "ItemAvaliadoComite.findByDescricao", query = "SELECT i FROM ItemAvaliadoComite i WHERE i.descricao = :descricao") })
public class ItemAvaliadoComite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "DESCRICAO")
	private String descricao;

	@JoinColumn(name = "ID_CONFIGURACAO_COMITE", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private ConfiguracaoComite configuracaoComite;

	public ItemAvaliadoComite() {
	}

	public ItemAvaliadoComite(Integer id) {
		this.id = id;
	}

	public ItemAvaliadoComite(Integer id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ConfiguracaoComite getConfiguracaoComite() {
		return configuracaoComite;
	}

	public void setConfiguracaoComite(ConfiguracaoComite configuracaoComite) {
		this.configuracaoComite = configuracaoComite;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ItemAvaliadoComite)) {
			return false;
		}
		ItemAvaliadoComite other = (ItemAvaliadoComite) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ItemAvaliadoComite[ id=" + id + " ]";
	}

}
