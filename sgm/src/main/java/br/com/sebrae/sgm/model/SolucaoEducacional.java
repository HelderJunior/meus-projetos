package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.NotEmpty;

import br.com.sebrae.sgm.model.enums.AtivoInativo;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "SOLUCAO_EDUCACIONAL")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "SolucaoEducacional.findAll", query = "SELECT se FROM SolucaoEducacional se") })
@Audited
public class SolucaoEducacional implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@NotEmpty(message = "Campo obrigatorio")
	@Basic(optional = false)
	@Column(name = "NOME")
	private String nome;

	@Basic(optional = false)
	@Column(name = "QUANTIDADE")
	private BigDecimal quantidade;

	@Column(name = "MEDIDA_BASE")
	private String medidaBase;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private AtivoInativo status = AtivoInativo.A;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumn(name = "ID_FORMA_AQUISICAO", referencedColumnName = "ID")
	@ManyToOne
	private FormaAquisicao formaAquisicao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "solucaoEducacional")
	private List<SolucaoEducacionalMeta> metasSolucaoEducacional;

	public SolucaoEducacional() {
	}

	public SolucaoEducacional(Integer id) {
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

	public FormaAquisicao getFormaAquisicao() {
		return formaAquisicao;
	}

	public void setFormaAquisicao(FormaAquisicao formaAquisicao) {
		this.formaAquisicao = formaAquisicao;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public AtivoInativo getStatus() {
		return status;
	}

	public void setStatus(AtivoInativo status) {
		this.status = status;
	}

	public String getMedidaBase() {
		return medidaBase;
	}

	public void setMedidaBase(String medidaBase) {
		this.medidaBase = medidaBase;
	}

	public List<SolucaoEducacionalMeta> getMetasSolucaoEducacional() {
		return metasSolucaoEducacional;
	}

	public void setMetasSolucaoEducacional(List<SolucaoEducacionalMeta> metasSolucaoEducacional) {
		this.metasSolucaoEducacional = metasSolucaoEducacional;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formaAquisicao == null) ? 0 : formaAquisicao.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((medidaBase == null) ? 0 : medidaBase.hashCode());
		result = prime * result + ((metasSolucaoEducacional == null) ? 0 : metasSolucaoEducacional.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((quantidade == null) ? 0 : quantidade.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolucaoEducacional other = (SolucaoEducacional) obj;
		if (formaAquisicao == null) {
			if (other.formaAquisicao != null)
				return false;
		} else if (!formaAquisicao.equals(other.formaAquisicao))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (medidaBase == null) {
			if (other.medidaBase != null)
				return false;
		} else if (!medidaBase.equals(other.medidaBase))
			return false;
		if (metasSolucaoEducacional == null) {
			if (other.metasSolucaoEducacional != null)
				return false;
		} else if (!metasSolucaoEducacional.equals(other.metasSolucaoEducacional))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (quantidade == null) {
			if (other.quantidade != null)
				return false;
		} else if (!quantidade.equals(other.quantidade))
			return false;
		if (status != other.status)
			return false;
		return true;
	}



}
