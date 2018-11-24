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
@Table(name = "RM_Acao")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "RMAcao.findAll", query = "SELECT r FROM RMAcao r"),
		@NamedQuery(name = "RMAcao.findByAcaoCod", query = "SELECT r FROM RMAcao r WHERE r.acaoCod = :acaoCod"),
		@NamedQuery(name = "RMAcao.findByAcaoNome", query = "SELECT r FROM RMAcao r WHERE r.acaoNome = :acaoNome") })
public class RMAcao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "AcaoCod")
	private String acaoCod;

	@Basic(optional = false)
	@Column(name = "AcaoNome")
	private String acaoNome;

	@JoinColumn(name = "PratifCod", referencedColumnName = "PratifCod")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Projeto pratifCod;

	public RMAcao() {
	}

	public RMAcao(String acaoCod) {
		this.acaoCod = acaoCod;
	}

	public RMAcao(String acaoCod, String acaoNome) {
		this.acaoCod = acaoCod;
		this.acaoNome = acaoNome;
	}

	public String getAcaoCod() {
		return acaoCod;
	}

	public void setAcaoCod(String acaoCod) {
		this.acaoCod = acaoCod;
	}

	public String getAcaoNome() {
		return acaoNome;
	}

	public void setAcaoNome(String acaoNome) {
		this.acaoNome = acaoNome;
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
		hash += (acaoCod != null ? acaoCod.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof RMAcao)) {
			return false;
		}
		RMAcao other = (RMAcao) object;
		if ((this.acaoCod == null && other.acaoCod != null)
				|| (this.acaoCod != null && !this.acaoCod.equals(other.acaoCod))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.RMAcao[ acaoCod=" + acaoCod + " ]";
	}

}
