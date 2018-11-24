package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "INDICADOR")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Indicador.findAll", query = "SELECT i FROM Indicador i order by i.nome"),
	@NamedQuery(name = "Indicador.findAllAtivos", query = "SELECT i FROM Indicador i where i.ativo = true order by i.nome"),
		@NamedQuery(name = "Indicador.findById", query = "SELECT i FROM Indicador i WHERE i.id = :id"),
		@NamedQuery(name = "Indicador.findByNome", query = "SELECT i FROM Indicador i WHERE i.nome = :nome") })
public class Indicador implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "NOME")
	private String nome;

	@Basic(optional = false)
	@Column(name = "ATIVO")
	private Boolean ativo = Boolean.TRUE;
	
	@Column(name = "UF")
	@Enumerated(EnumType.STRING)
	private UF uf;
	
	@Column(name = "DESCRICAO")
	private String descricao;
	
	@Column(name = "JUSTIFICATIVA")
	private String justificativa;

	@OneToMany(mappedBy = "indicador")
	private List<Meta> metas;

	public Indicador() {
	}

	public Indicador(Integer id) {
		this.id = id;
	}

	public Indicador(Integer id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Indicador)) {
			return false;
		}
		Indicador other = (Indicador) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Indicador[ id=" + id + " ]";
	}

}
