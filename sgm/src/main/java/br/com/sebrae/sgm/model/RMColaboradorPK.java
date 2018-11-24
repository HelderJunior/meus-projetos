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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import br.com.sebrae.sgm.model.enums.UF;

/**
 * 
 * @author Diego
 */
@Embeddable
public class RMColaboradorPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "ColaboradorCpf")
	private String colaboradorCpf;

	@Basic(optional = false)
	@Column(name = "ColaboradorMatricula")
	private String colaboradorMatricula;

	@Basic(optional = false)
	@Column(name = "UnidadeUf")
	@Enumerated(EnumType.STRING)
	private UF unidadeUf;

	public RMColaboradorPK() {
	}

	public RMColaboradorPK(String colaboradorCpf, String colaboradorMatricula, UF unidadeUf) {
		this.colaboradorCpf = colaboradorCpf;
		this.colaboradorMatricula = colaboradorMatricula;
		this.unidadeUf = unidadeUf;
	}

	public String getColaboradorCpf() {
		return colaboradorCpf;
	}

	public void setColaboradorCpf(String colaboradorCpf) {
		this.colaboradorCpf = colaboradorCpf;
	}

	public String getColaboradorMatricula() {
		return colaboradorMatricula;
	}

	public void setColaboradorMatricula(String colaboradorMatricula) {
		this.colaboradorMatricula = colaboradorMatricula;
	}

	public UF getUnidadeUf() {
		return unidadeUf;
	}

	public void setUnidadeUf(UF unidadeUf) {
		this.unidadeUf = unidadeUf;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (getColaboradorCpf() != null ? getColaboradorCpf().hashCode() : 0);
		hash += (getColaboradorMatricula() != null ? getColaboradorMatricula().hashCode() : 0);
		hash += (getUnidadeUf() != null ? getUnidadeUf().hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof RMColaboradorPK)) {
			return false;
		}
		RMColaboradorPK other = (RMColaboradorPK) object;
		if ((this.getColaboradorCpf() == null && other.getColaboradorCpf() != null)
				|| (this.getColaboradorCpf() != null && !this.getColaboradorCpf().equals(other.getColaboradorCpf()))) {
			return false;
		}
		if ((this.getColaboradorMatricula() == null && other.getColaboradorMatricula() != null)
				|| (this.getColaboradorMatricula() != null && !this.getColaboradorMatricula().equals(
						other.getColaboradorMatricula()))) {
			return false;
		}
		if ((this.getUnidadeUf() == null && other.getUnidadeUf() != null)
				|| (this.getUnidadeUf() != null && !this.getUnidadeUf().equals(other.getUnidadeUf()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "colaboradorCpf-" + colaboradorCpf + "colaboradorMatricula-" + colaboradorMatricula + "unidadeUf-"
				+ unidadeUf;
	}

}
