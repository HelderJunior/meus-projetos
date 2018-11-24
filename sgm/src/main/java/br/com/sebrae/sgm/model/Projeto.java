package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.UF;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "RM_Pratif")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Projeto.findAll", query = "SELECT p FROM Projeto p where p.uf = :uf order by p.nome"),
		@NamedQuery(name = "Projeto.findById", query = "SELECT p FROM Projeto p WHERE p.id = :id"),
		@NamedQuery(name = "Projeto.findByNome", query = "SELECT p FROM Projeto p WHERE p.nome = :nome") })
public class Projeto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "PratifCod")
	private String id;

	@Basic(optional = false)
	@Column(name = "PratifNome")
	private String nome;

	@Basic(optional = false)
	@Column(name = "PratifUF")
	@Enumerated(EnumType.STRING)
	private UF uf;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pratifCod", fetch = FetchType.LAZY)
	private List<RMAcao> acoes;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pratifCod", fetch = FetchType.LAZY)
	private List<RMResultado> resultados;

	@ManyToMany(mappedBy = "projetosVinculados")
	private List<Meta> metas;

	public Projeto() {
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Meta> getMetas() {
		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public List<RMAcao> getAcoes() {
		return acoes;
	}

	public void setAcoes(List<RMAcao> acoes) {
		this.acoes = acoes;
	}

	public List<RMResultado> getResultados() {
		return resultados;
	}

	public void setResultados(List<RMResultado> resultados) {
		this.resultados = resultados;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Projeto)) {
			return false;
		}
		Projeto other = (Projeto) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Projeto[ id=" + id + " ]";
	}

}
