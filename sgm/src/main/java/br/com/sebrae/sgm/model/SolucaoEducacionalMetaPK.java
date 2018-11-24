/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sebrae.sgm.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author Diego
 */
@Embeddable
public class SolucaoEducacionalMetaPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "ID_SOLUCAO_EDUCACIONAL")
	private Integer idSolucaoEducacional;

	@Basic(optional = false)
	@Column(name = "ID_META")
	private Integer idMeta;

	public SolucaoEducacionalMetaPK() {
	}

	public SolucaoEducacionalMetaPK(Integer idSolucaoEducacional, Integer idMeta) {
		this.idSolucaoEducacional = idSolucaoEducacional;
		this.idMeta = idMeta;
	}

	public Integer getIdSolucaoEducacional() {
		return idSolucaoEducacional;
	}

	public void setIdSolucaoEducacional(Integer idSolucaoEducacional) {
		this.idSolucaoEducacional = idSolucaoEducacional;
	}

	public Integer getIdMeta() {
		return idMeta;
	}

	public void setIdMeta(Integer idMeta) {
		this.idMeta = idMeta;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (int) idSolucaoEducacional;
		hash += (int) idMeta;
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SolucaoEducacionalMetaPK)) {
			return false;
		}
		SolucaoEducacionalMetaPK other = (SolucaoEducacionalMetaPK) object;
		if (this.getIdSolucaoEducacional() != other.getIdSolucaoEducacional()) {
			return false;
		}
		if (this.getIdMeta() != other.getIdMeta()) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.SolucaoEducacionalMetaPK[ idSolucaoEducacional=" + idSolucaoEducacional
				+ ", idMeta=" + idMeta + " ]";
	}

}
