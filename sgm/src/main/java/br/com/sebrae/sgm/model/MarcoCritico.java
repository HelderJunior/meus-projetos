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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

import br.com.sebrae.sgm.model.enums.Mes;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "MARCO_CRITICO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "MarcoCritico.findAll", query = "SELECT m FROM MarcoCritico m"),
		@NamedQuery(name = "MarcoCritico.findById", query = "SELECT m FROM MarcoCritico m WHERE m.id = :id"),
		@NamedQuery(name = "MarcoCritico.findByQuantidade", query = "SELECT m FROM MarcoCritico m WHERE m.quantidade = :quantidade"),
		@NamedQuery(name = "MarcoCritico.findByPrazoConclusao", query = "SELECT m FROM MarcoCritico m WHERE m.prazoConclusao = :prazoConclusao"),
		@NamedQuery(name = "MarcoCritico.findByEvidenciaEntrega", query = "SELECT m FROM MarcoCritico m WHERE m.evidenciaEntrega = :evidenciaEntrega") })
@Audited
public class MarcoCritico implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "QUANTIDADE")
	private Integer quantidade;

	@Basic(optional = false)
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "PRAZO_CONCLUSAO")
	private Mes prazoConclusao;

	@Basic(optional = false)
	@Column(name = "EVIDENCIA_ENTREGA")
	private String evidenciaEntrega;

	@JoinColumn(name = "ID_META", referencedColumnName = "ID")
	@ManyToOne
	private Meta meta;

	@Transient
	private Boolean editando = Boolean.FALSE;

	public MarcoCritico() {
	}

	public MarcoCritico(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Mes getPrazoConclusao() {
		return prazoConclusao;
	}

	public void setPrazoConclusao(Mes prazoConclusao) {
		this.prazoConclusao = prazoConclusao;
	}

	public String getEvidenciaEntrega() {
		return evidenciaEntrega;
	}

	public void setEvidenciaEntrega(String evidenciaEntrega) {
		this.evidenciaEntrega = evidenciaEntrega;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof MarcoCritico)) {
			return false;
		}
		MarcoCritico other = (MarcoCritico) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.MarcoCritico[ id=" + id + " ]";
	}

}
