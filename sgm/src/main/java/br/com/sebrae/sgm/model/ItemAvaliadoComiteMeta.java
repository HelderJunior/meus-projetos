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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.com.sebrae.sgm.model.enums.ResultadoAvaliacaoComite;

/**
 * @author Diego
 */
@Entity
@Table(name = "ITEM_AVALIACAO_COMITE_META")
@XmlRootElement
@Audited
public class ItemAvaliadoComiteMeta implements Serializable {

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
	@Column(name = "RESULTADO_AVALIACAO")
	@Enumerated(EnumType.STRING)
	private ResultadoAvaliacaoComite resultado;

	@JoinColumn(name = "ID_META", referencedColumnName = "ID")
	@ManyToOne
	private Meta meta;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumn(name = "ID_ITEM_AVALIADO_COMITE", referencedColumnName = "ID")
	@ManyToOne
	private ItemAvaliadoComite itemAvaliado;

	public ItemAvaliadoComiteMeta() {
	}

	public ItemAvaliadoComiteMeta(Integer id) {
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

	public ResultadoAvaliacaoComite getResultado() {
		return resultado;
	}

	public void setResultado(ResultadoAvaliacaoComite resultado) {
		this.resultado = resultado;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public ItemAvaliadoComite getItemAvaliado() {
		return itemAvaliado;
	}

	public void setItemAvaliado(ItemAvaliadoComite itemAvaliado) {
		this.itemAvaliado = itemAvaliado;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ItemAvaliadoComiteMeta)) {
			return false;
		}
		ItemAvaliadoComiteMeta other = (ItemAvaliadoComiteMeta) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ItemAvaliacao[ id=" + id + " ]";
	}

}
