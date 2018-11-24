package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.com.sebrae.sgm.model.enums.StatusLeitura;
import br.com.sebrae.sgm.model.enums.TipoCampoObservacao;
import br.com.sebrae.sgm.model.enums.TipoObservacao;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "OBSERVACAO_CAMPO")
@XmlRootElement
@Audited
public class ObservacaoCampo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "DESCRICAO")
	private String descricao;

	@Basic(optional = false)
	@Column(name = "DATA_HORA")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataHora = new Date();

	@Basic(optional = false)
	@Column(name = "TIPO")
	@Enumerated(EnumType.STRING)
	private TipoObservacao tipo;

	@Basic(optional = false)
	@Column(name = "CAMPO")
	@Enumerated(EnumType.STRING)
	private TipoCampoObservacao campo;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private StatusLeitura status = StatusLeitura.N;

	@JoinColumn(name = "ID_META", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Meta meta;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumn(name = "ID_REMETENTE", referencedColumnName = "ID")
	@ManyToOne
	private Usuario remetente;

	public ObservacaoCampo() {
	}

	public ObservacaoCampo(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public TipoObservacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoObservacao tipo) {
		this.tipo = tipo;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Usuario getRemetente() {
		return remetente;
	}

	public void setRemetente(Usuario remetente) {
		this.remetente = remetente;
	}

	public TipoCampoObservacao getCampo() {
		return campo;
	}

	public void setCampo(TipoCampoObservacao campo) {
		this.campo = campo;
	}

	public StatusLeitura getStatus() {
		return status;
	}

	public void setStatus(StatusLeitura status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ObservacaoCampo)) {
			return false;
		}
		ObservacaoCampo other = (ObservacaoCampo) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Observacao[ id=" + id + " ]";
	}

}
