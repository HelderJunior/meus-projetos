package br.com.sebrae.sgm.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "RM_LogEspOcup")
@XmlRootElement
public class RMLogEspOcup implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected RMLogEspOcupPK rmLogEspOcupPK;

	@JoinColumns({
			@JoinColumn(name = "ColaboradorCpf", referencedColumnName = "ColaboradorCpf", insertable = false, updatable = false),
			@JoinColumn(name = "ColaboradorMatricula", referencedColumnName = "ColaboradorMatricula", insertable = false, updatable = false),
			@JoinColumn(name = "UnidadeUf", referencedColumnName = "UnidadeUf", insertable = false, updatable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private RMColaborador rmColaborador;

	@JoinColumns({
			@JoinColumn(name = "EspOcupEspacoOcupacionalUf", referencedColumnName = "EspacoOcupacionalUF", insertable = false, updatable = false),
			@JoinColumn(name = "EspOcupEspacoOcupacionalCodEspOcup", referencedColumnName = "EspacoOcupacionalCodEspOcup", insertable = false, updatable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private EspacoOcupacional rmEspacoOcupacional;

	public RMLogEspOcup() {
	}

	public RMLogEspOcupPK getRmLogEspOcupPK() {
		return rmLogEspOcupPK;
	}

	public void setRmLogEspOcupPK(RMLogEspOcupPK rmLogEspOcupPK) {
		this.rmLogEspOcupPK = rmLogEspOcupPK;
	}

	public RMColaborador getRmColaborador() {
		return rmColaborador;
	}

	public void setRmColaborador(RMColaborador rmColaborador) {
		this.rmColaborador = rmColaborador;
	}

	public EspacoOcupacional getRmEspacoOcupacional() {
		return rmEspacoOcupacional;
	}

	public void setRmEspacoOcupacional(EspacoOcupacional rmEspacoOcupacional) {
		this.rmEspacoOcupacional = rmEspacoOcupacional;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rmLogEspOcupPK == null) ? 0 : rmLogEspOcupPK.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RMLogEspOcup other = (RMLogEspOcup) obj;
		if (rmLogEspOcupPK == null) {
			if (other.rmLogEspOcupPK != null)
				return false;
		} else if (!rmLogEspOcupPK.equals(other.rmLogEspOcupPK))
			return false;
		return true;
	}

}
