package br.com.sebrae.sgm.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Diego
 */
@Embeddable
public class PorcentagemRemuneracaoVariavelPK implements Serializable {
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "QTD_METAS_ESTABELECIDAS")
	private Integer qtdMetasEstabelecidas;

	@Basic(optional = false)
	@Column(name = "QTD_METAS_ATINGIDAS")
	private Integer qtdMetasAtingidas;

	@Basic(optional = false)
	@Column(name = "ID_CONFIGURACAO_METAS")
	private Integer idConfiguracaoMetas;

	public PorcentagemRemuneracaoVariavelPK() {
	}

	public PorcentagemRemuneracaoVariavelPK(Integer qtdMetasEstabelecidas, Integer qtdMetasAtingidas,
			Integer idConfiguracaoMetas) {
		this.qtdMetasEstabelecidas = qtdMetasEstabelecidas;
		this.qtdMetasAtingidas = qtdMetasAtingidas;
		this.idConfiguracaoMetas = idConfiguracaoMetas;
	}

	public Integer getQtdMetasEstabelecidas() {
		return qtdMetasEstabelecidas;
	}

	public void setQtdMetasEstabelecidas(Integer qtdMetasEstabelecidas) {
		this.qtdMetasEstabelecidas = qtdMetasEstabelecidas;
	}

	public Integer getQtdMetasAtingidas() {
		return qtdMetasAtingidas;
	}

	public void setQtdMetasAtingidas(Integer qtdMetasAtingidas) {
		this.qtdMetasAtingidas = qtdMetasAtingidas;
	}

	public Integer getIdConfiguracaoMetas() {
		return idConfiguracaoMetas;
	}

	public void setIdConfiguracaoMetas(Integer idConfiguracaoMetas) {
		this.idConfiguracaoMetas = idConfiguracaoMetas;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (int) qtdMetasEstabelecidas;
		hash += (int) qtdMetasAtingidas;
		hash += (int) idConfiguracaoMetas;
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof PorcentagemRemuneracaoVariavelPK)) {
			return false;
		}
		PorcentagemRemuneracaoVariavelPK other = (PorcentagemRemuneracaoVariavelPK) object;
		if (this.qtdMetasEstabelecidas != other.qtdMetasEstabelecidas) {
			return false;
		}
		if (this.qtdMetasAtingidas != other.qtdMetasAtingidas) {
			return false;
		}
		if (this.idConfiguracaoMetas != other.idConfiguracaoMetas) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.PorcentagemRemuneracaoVariavelPK[ qtdMetasEstabelecidas="
				+ qtdMetasEstabelecidas + ", qtdMetasAtingidas=" + qtdMetasAtingidas + ", idConfiguracaoMetas="
				+ idConfiguracaoMetas + " ]";
	}

}
