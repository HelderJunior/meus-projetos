package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.UF;

/**
 * @author Diego
 */
@Entity
@Table(name = "RM_ESPACOOCUPACIONAL")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "EspacoOcupacional.findAll", query = "SELECT e FROM EspacoOcupacional e where e.situacao = '1' order by e.descricao"),
		@NamedQuery(name = "EspacoOcupacional.findByUf", query = "SELECT e FROM EspacoOcupacional e WHERE e.espacoOcupacionalPK.uf = :uf"),
		@NamedQuery(name = "EspacoOcupacional.findByCodigo", query = "SELECT e FROM EspacoOcupacional e WHERE e.espacoOcupacionalPK.codigo = :codigo"),
		@NamedQuery(name = "EspacoOcupacional.findByDescricao", query = "SELECT e FROM EspacoOcupacional e WHERE e.descricao = :descricao"),
		@NamedQuery(name = "EspacoOcupacional.findBySituacao", query = "SELECT e FROM EspacoOcupacional e WHERE e.situacao = :situacao") })
public class EspacoOcupacional implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected EspacoOcupacionalPK espacoOcupacionalPK;

	@Column(name = "EspacoOcupacionalDescricao")
	private String descricao;

	@Basic(optional = false)
	@Column(name = "EspacoOcupacionalSituacao")
	private Character situacao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "rmEspacoOcupacional", fetch = FetchType.LAZY)
	private List<RMColaborador> rMColaboradorList;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "rmEspacoOcupacional", fetch = FetchType.LAZY)
	private List<RMLogEspOcup> rMLogEspOcupList;

	public EspacoOcupacional() {
	}

	public EspacoOcupacional(EspacoOcupacionalPK espacoOcupacionalPK) {
		this.espacoOcupacionalPK = espacoOcupacionalPK;
	}

	public EspacoOcupacional(EspacoOcupacionalPK espacoOcupacionalPK, Character situacao) {
		this.espacoOcupacionalPK = espacoOcupacionalPK;
		this.situacao = situacao;
	}

	public EspacoOcupacional(UF uf, String codigo) {
		this.espacoOcupacionalPK = new EspacoOcupacionalPK(uf, codigo);
	}

	public String getDescricaoComCodigo() {
		return getEspacoOcupacionalPK().getCodigo() + " - " + this.getDescricao();
	}

	public EspacoOcupacionalPK getEspacoOcupacionalPK() {
		return espacoOcupacionalPK;
	}

	public void setEspacoOcupacionalPK(EspacoOcupacionalPK espacoOcupacionalPK) {
		this.espacoOcupacionalPK = espacoOcupacionalPK;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Character getSituacao() {
		return situacao;
	}

	public void setSituacao(Character situacao) {
		this.situacao = situacao;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (getEspacoOcupacionalPK() != null ? getEspacoOcupacionalPK().hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EspacoOcupacional)) {
			return false;
		}
		EspacoOcupacional other = (EspacoOcupacional) object;
		if ((this.getEspacoOcupacionalPK() == null && other.getEspacoOcupacionalPK() != null)
				|| (this.getEspacoOcupacionalPK() != null && !this.getEspacoOcupacionalPK().equals(
						other.getEspacoOcupacionalPK()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getEspacoOcupacionalPK() + " - " + this.descricao;
	}

}
