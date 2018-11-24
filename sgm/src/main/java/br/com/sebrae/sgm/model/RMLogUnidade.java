package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "RM_LogUnidade")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "RMLogUnidade.findAll", query = "SELECT r FROM RMLogUnidade r"),
		@NamedQuery(name = "RMLogUnidade.findByLogUnidadeDataMudanca", query = "SELECT r FROM RMLogUnidade r WHERE r.rMLogUnidadePK.logUnidadeDataMudanca = :logUnidadeDataMudanca"),
		@NamedQuery(name = "RMLogUnidade.findByColaboradorColaboradorCpf", query = "SELECT r FROM RMLogUnidade r WHERE r.rMLogUnidadePK.colaboradorColaboradorCpf = :colaboradorColaboradorCpf"),
		@NamedQuery(name = "RMLogUnidade.findByColaboradorColaboradorMatricula", query = "SELECT r FROM RMLogUnidade r WHERE r.rMLogUnidadePK.colaboradorColaboradorMatricula = :colaboradorColaboradorMatricula"),
		@NamedQuery(name = "RMLogUnidade.findByColaboradorUnidadeUf", query = "SELECT r FROM RMLogUnidade r WHERE r.rMLogUnidadePK.colaboradorUnidadeUf = :colaboradorUnidadeUf"),
		@NamedQuery(name = "RMLogUnidade.findByUnidadeUnidadeCodUnidade", query = "SELECT r FROM RMLogUnidade r WHERE r.rMLogUnidadePK.unidadeUnidadeCodUnidade = :unidadeUnidadeCodUnidade"),
		@NamedQuery(name = "RMLogUnidade.findByUnidadeUnidadeUf", query = "SELECT r FROM RMLogUnidade r WHERE r.rMLogUnidadePK.unidadeUnidadeUf = :unidadeUnidadeUf") })
public class RMLogUnidade implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected RMLogUnidadePK rMLogUnidadePK;

	@JoinColumns({
			@JoinColumn(name = "ColaboradorColaboradorCpf", referencedColumnName = "ColaboradorCpf", insertable = false, updatable = false),
			@JoinColumn(name = "ColaboradorColaboradorMatricula", referencedColumnName = "ColaboradorMatricula", insertable = false, updatable = false),
			@JoinColumn(name = "ColaboradorUnidadeUf", referencedColumnName = "UnidadeUf", insertable = false, updatable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private RMColaborador rmColaborador;

	@JoinColumns({
			@JoinColumn(name = "UnidadeUnidadeUf", referencedColumnName = "UnidadeUf", insertable = false, updatable = false),
			@JoinColumn(name = "UnidadeUnidadeCodUnidade", referencedColumnName = "UnidadeCodUnidade", insertable = false, updatable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Unidade rmUnidade;

	public RMLogUnidade() {
	}

	public RMLogUnidade(RMLogUnidadePK rMLogUnidadePK) {
		this.rMLogUnidadePK = rMLogUnidadePK;
	}

	public RMLogUnidade(Date logUnidadeDataMudanca, String colaboradorColaboradorCpf,
			String colaboradorColaboradorMatricula, String colaboradorUnidadeUf, String unidadeUnidadeCodUnidade,
			String unidadeUnidadeUf) {
		this.rMLogUnidadePK = new RMLogUnidadePK(logUnidadeDataMudanca, colaboradorColaboradorCpf,
				colaboradorColaboradorMatricula, colaboradorUnidadeUf, unidadeUnidadeCodUnidade, unidadeUnidadeUf);
	}

	public RMLogUnidadePK getRMLogUnidadePK() {
		return rMLogUnidadePK;
	}

	public void setRMLogUnidadePK(RMLogUnidadePK rMLogUnidadePK) {
		this.rMLogUnidadePK = rMLogUnidadePK;
	}

	public RMColaborador getRmColaborador() {
		return rmColaborador;
	}

	public void setRmColaborador(RMColaborador rmColaborador) {
		this.rmColaborador = rmColaborador;
	}

	public Unidade getRmUnidade() {
		return rmUnidade;
	}

	public void setRmUnidade(Unidade rmUnidade) {
		this.rmUnidade = rmUnidade;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (rMLogUnidadePK != null ? rMLogUnidadePK.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof RMLogUnidade)) {
			return false;
		}
		RMLogUnidade other = (RMLogUnidade) object;
		if ((this.rMLogUnidadePK == null && other.rMLogUnidadePK != null)
				|| (this.rMLogUnidadePK != null && !this.rMLogUnidadePK.equals(other.rMLogUnidadePK))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.RMLogUnidade[ rMLogUnidadePK=" + rMLogUnidadePK + " ]";
	}

}
