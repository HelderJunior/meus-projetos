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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.NotEmpty;

import br.com.sebrae.sgm.model.enums.StatusEnvio;
import br.com.sebrae.sgm.model.enums.TipoObservacao;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "OBSERVACAO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Observacao.findAll", query = "SELECT o FROM Observacao o"),
		@NamedQuery(name = "Observacao.findById", query = "SELECT o FROM Observacao o WHERE o.id = :id"),
		@NamedQuery(name = "Observacao.findByDescricao", query = "SELECT o FROM Observacao o WHERE o.descricao = :descricao"),
		@NamedQuery(name = "Observacao.findByResposta", query = "SELECT o FROM Observacao o WHERE o.resposta = :resposta"),
		@NamedQuery(name = "Observacao.findByDataHora", query = "SELECT o FROM Observacao o WHERE o.dataHora = :dataHora"),
		@NamedQuery(name = "Observacao.findByTipo", query = "SELECT o FROM Observacao o WHERE o.tipo = :tipo") })
@Audited
public class Observacao implements Serializable {
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

	@Column(name = "RESPOSTA")
	private String resposta;

	@Column(name = "DATA_HORA_RESPOSTA")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataHoraResposta;

	@Basic(optional = false)
	@Column(name = "TIPO")
	@Enumerated(EnumType.STRING)
	private TipoObservacao tipo;

	@JoinColumn(name = "ID_META", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Meta meta;

	@Column(name = "CHAVE_PERFIL_DESTINO")
	private String chavePerfilDestino = Perfil.COLABORADOR_CHAVE;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private StatusEnvio status = StatusEnvio.E;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumn(name = "ID_REMETENTE", referencedColumnName = "ID")
	@ManyToOne
	private Usuario remetente;

	public Observacao() {
	}

	public Observacao(Integer id) {
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

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
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

	public Date getDataHoraResposta() {
		return dataHoraResposta;
	}

	public void setDataHoraResposta(Date dataHoraResposta) {
		this.dataHoraResposta = dataHoraResposta;
	}

	public String getChavePerfilDestino() {
		return chavePerfilDestino;
	}

	public void setChavePerfilDestino(String chavePerfilDestino) {
		this.chavePerfilDestino = chavePerfilDestino;
	}

	public StatusEnvio getStatus() {
		return status;
	}

	public void setStatus(StatusEnvio status) {
		this.status = status;
	}

	public Usuario getRemetente() {
		return remetente;
	}

	public void setRemetente(Usuario remetente) {
		this.remetente = remetente;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Observacao)) {
			return false;
		}
		Observacao other = (Observacao) object;
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
