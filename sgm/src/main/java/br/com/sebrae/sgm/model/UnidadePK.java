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
public class UnidadePK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "UnidadeUf")
	@Enumerated(EnumType.STRING)
	private UF uf;

	@Basic(optional = false)
	@Column(name = "UnidadeCodUnidade")
	private String codigo;

	public UnidadePK() {
	}

	public UnidadePK(UF uf, String codigo) {
		this.uf = uf;
		this.codigo = codigo;
	}

	public String getCodigoSemPonto() {
		return codigo.replaceAll("\\.", "_");
	}
	
	public String getCodigoSemPonto2() {
		return codigo.replaceAll("\\.", "");
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (uf != null ? uf.hashCode() : 0);
		hash += (codigo != null ? codigo.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof UnidadePK)) {
			return false;
		}
		UnidadePK other = (UnidadePK) object;
		if ((this.getUf() == null && other.getUf() != null)
				|| (this.getUf() != null && !this.getUf().equals(other.getUf()))) {
			return false;
		}
		if ((this.getCodigo() == null && other.getCodigo() != null)
				|| (this.getCodigo() != null && !this.getCodigo().equals(other.getCodigo()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return uf + "-" + codigo;
	}

}
