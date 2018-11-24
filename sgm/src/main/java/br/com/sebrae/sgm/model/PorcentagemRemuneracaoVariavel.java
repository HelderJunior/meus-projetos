package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "PORCENTAGEM_REMUNERACAO_VARIAVEL")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "PorcentagemRemuneracaoVariavel.findAll", query = "SELECT p FROM PorcentagemRemuneracaoVariavel p"),
		@NamedQuery(name = "PorcentagemRemuneracaoVariavel.findByPercentualRemuneracaoVariavel", query = "SELECT p FROM PorcentagemRemuneracaoVariavel p WHERE p.percentualRemuneracaoVariavel = :percentualRemuneracaoVariavel") })
public class PorcentagemRemuneracaoVariavel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected PorcentagemRemuneracaoVariavelPK pk;

	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce
	// field validation
	@Column(name = "PERCENTUAL_REMUNERACAO_VARIAVEL")
	private BigDecimal percentualRemuneracaoVariavel;

	@JoinColumn(name = "ID_CONFIGURACAO_METAS", referencedColumnName = "ID", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private ConfiguracaoMetas configuracaoMetas;

	public PorcentagemRemuneracaoVariavel() {
	}

	public PorcentagemRemuneracaoVariavel(PorcentagemRemuneracaoVariavelPK pk) {
		super();
		this.pk = pk;
	}

	public BigDecimal getPercentualRemuneracaoVariavel() {
		return percentualRemuneracaoVariavel;
	}

	public void setPercentualRemuneracaoVariavel(BigDecimal percentualRemuneracaoVariavel) {
		this.percentualRemuneracaoVariavel = percentualRemuneracaoVariavel;
	}

	public ConfiguracaoMetas getConfiguracaoMetas() {
		return configuracaoMetas;
	}

	public void setConfiguracaoMetas(ConfiguracaoMetas configuracaoMetas) {
		this.configuracaoMetas = configuracaoMetas;
	}

	public PorcentagemRemuneracaoVariavelPK getPk() {
		return pk;
	}

	public void setPk(PorcentagemRemuneracaoVariavelPK pk) {
		this.pk = pk;
	}

	@PrePersist
	public void updateChave() {
		this.pk.setIdConfiguracaoMetas(this.configuracaoMetas.getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pk == null) ? 0 : pk.hashCode());
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
		PorcentagemRemuneracaoVariavel other = (PorcentagemRemuneracaoVariavel) obj;
		if (pk == null) {
			if (other.pk != null)
				return false;
		} else if (!pk.equals(other.pk))
			return false;
		return true;
	}

}
