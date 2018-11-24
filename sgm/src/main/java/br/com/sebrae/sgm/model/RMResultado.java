package br.com.sebrae.sgm.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "RM_Resultado")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "RMResultado.findAll", query = "SELECT r FROM RMResultado r"),
		@NamedQuery(name = "RMResultado.findByResultadoCod", query = "SELECT r FROM RMResultado r WHERE r.resultadoCod = :resultadoCod"),
		@NamedQuery(name = "RMResultado.findByResultadoNome", query = "SELECT r FROM RMResultado r WHERE r.resultadoNome = :resultadoNome") })
public class RMResultado implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "ResultadoCod")
	private String resultadoCod;

	@Basic(optional = false)
	@Column(name = "ResultadoNome")
	private String resultadoNome;

	@JoinColumn(name = "PratifCod", referencedColumnName = "PratifCod")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Projeto pratifCod;

	public RMResultado() {
	}

	public RMResultado(String resultadoCod) {
		this.resultadoCod = resultadoCod;
	}

	public RMResultado(String resultadoCod, String resultadoNome) {
		this.resultadoCod = resultadoCod;
		this.resultadoNome = resultadoNome;
	}

	public String getResultadoCod() {
		return resultadoCod;
	}

	public void setResultadoCod(String resultadoCod) {
		this.resultadoCod = resultadoCod;
	}

	public String getResultadoNome() {
		return resultadoNome;
	}

	public void setResultadoNome(String resultadoNome) {
		this.resultadoNome = resultadoNome;
	}

	public Projeto getPratifCod() {
		return pratifCod;
	}

	public void setPratifCod(Projeto pratifCod) {
		this.pratifCod = pratifCod;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (resultadoCod != null ? resultadoCod.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof RMResultado)) {
			return false;
		}
		RMResultado other = (RMResultado) object;
		if ((this.resultadoCod == null && other.resultadoCod != null)
				|| (this.resultadoCod != null && !this.resultadoCod.equals(other.resultadoCod))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.RMResultado[ resultadoCod=" + resultadoCod + " ]";
	}

}
