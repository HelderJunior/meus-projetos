package br.com.sebrae.sgm.model;

import java.io.Serializable;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.AtivoInativo;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "OBJETIVO_ESTRATEGICO")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "ObjetivoEstrategico.findAll", query = "SELECT obj FROM ObjetivoEstrategico obj") })
public class ObjetivoEstrategico implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "NOME")
	private String nome;

	@Column(name = "DESCRICAO")
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private AtivoInativo status;

	@Transient
	private Boolean selecionado;

	public ObjetivoEstrategico() {
	}

	public ObjetivoEstrategico(Integer id) {
		this.id = id;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public AtivoInativo getStatus() {
		return status;
	}

	public void setStatus(AtivoInativo status) {
		this.status = status;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ObjetivoEstrategico)) {
			return false;
		}
		ObjetivoEstrategico other = (ObjetivoEstrategico) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

}
