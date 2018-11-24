package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.sebrae.sgm.model.enums.TipoMeta;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "CONFIGURACAO_META")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ConfiguracaoMetas.findAll", query = "SELECT c FROM ConfiguracaoMetas c"),
		@NamedQuery(name = "ConfiguracaoMetas.findById", query = "SELECT c FROM ConfiguracaoMetas c WHERE c.id = :id"),
		@NamedQuery(name = "ConfiguracaoMetas.findByQtdMinimaMetasIndividuaisPactuadasCiclo", query = "SELECT c FROM ConfiguracaoMetas c WHERE c.qtdMinimaMetasIndividuaisPactuadasCiclo = :qtdMinimaMetasIndividuaisPactuadasCiclo"),
		@NamedQuery(name = "ConfiguracaoMetas.findByQtdMaximaMetasIndividuaisPactuadasCiclo", query = "SELECT c FROM ConfiguracaoMetas c WHERE c.qtdMaximaMetasIndividuaisPactuadasCiclo = :qtdMaximaMetasIndividuaisPactuadasCiclo"),
		@NamedQuery(name = "ConfiguracaoMetas.findByImpactaRemuneracaoVariavel", query = "SELECT c FROM ConfiguracaoMetas c WHERE c.impactaRemuneracaoVariavel = :impactaRemuneracaoVariavel"),
		@NamedQuery(name = "ConfiguracaoMetas.findByTipoMeta", query = "SELECT c FROM ConfiguracaoMetas c WHERE c.tipoMeta = :tipoMeta") })
public class ConfiguracaoMetas implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "QTD_MINIMA_METAS_INDIVIDUAIS_PACTUADAS_CICLO")
	private Integer qtdMinimaMetasIndividuaisPactuadasCiclo;

	@Basic(optional = false)
	@Column(name = "QTD_MAXIMA_METAS_INDIVIDUAIS_PACTUADAS_CICLO")
	private Integer qtdMaximaMetasIndividuaisPactuadasCiclo;

	@Basic(optional = false)
	@Column(name = "IMPACTA_REMUNERACAO_VARIAVEL")
	private Boolean impactaRemuneracaoVariavel = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "TIPO_META")
	@Enumerated(EnumType.STRING)
	private TipoMeta tipoMeta;

	@Column(name = "PONTUACAO_MAXIMA_FUNDAMENTAL",  scale = 5, precision = 2)
	private BigDecimal pontuacaoMaximaFundamental;

	@Column(name = "PONTUACAO_MAXIMA_COMPLEMENTAR", scale = 5, precision = 2)
	private BigDecimal pontuacaoMaximaComplementar;

	@Column(name = "CALCULO_HORAS_FUNDAMENTAL", scale = 5, precision = 2)
	private BigDecimal calculoHorasFundamental;

	@Column(name = "CALCULO_HORAS_COMPLEMENTAR", scale = 5, precision = 2)
	private BigDecimal calculoHorasComplementar;

	@JoinTable(name = "CONFIGURACAO_META_ESPACO_OCUPACIONAL", joinColumns = { @JoinColumn(name = "ID", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "UF", referencedColumnName = "EspacoOcupacionalUF"),
			@JoinColumn(name = "CODIGO", referencedColumnName = "EspacoOcupacionalCodEspOcup") })
	@ManyToMany
	private List<EspacoOcupacional> espacosOcupacionais;

	@JoinTable(name = "CONFIGURACAO_METAS_UNIDADES", joinColumns = { @JoinColumn(name = "ID_CONFIGURACAO_METAS", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "UF", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToMany
	private List<Unidade> unidades;

	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne
	private CicloConfiguracao cicloConfiguracao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoMetas", orphanRemoval = true)
	private List<PorcentagemRemuneracaoVariavel> porcentagensRemuneracaoVariavel;

	public ConfiguracaoMetas() {
	}

	public ConfiguracaoMetas(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQtdMinimaMetasIndividuaisPactuadasCiclo() {
		return qtdMinimaMetasIndividuaisPactuadasCiclo;
	}

	public void setQtdMinimaMetasIndividuaisPactuadasCiclo(Integer qtdMinimaMetasIndividuaisPactuadasCiclo) {
		this.qtdMinimaMetasIndividuaisPactuadasCiclo = qtdMinimaMetasIndividuaisPactuadasCiclo;
	}

	public Integer getQtdMaximaMetasIndividuaisPactuadasCiclo() {
		return qtdMaximaMetasIndividuaisPactuadasCiclo;
	}

	public void setQtdMaximaMetasIndividuaisPactuadasCiclo(Integer qtdMaximaMetasIndividuaisPactuadasCiclo) {
		this.qtdMaximaMetasIndividuaisPactuadasCiclo = qtdMaximaMetasIndividuaisPactuadasCiclo;
	}

	public Boolean getImpactaRemuneracaoVariavel() {
		return impactaRemuneracaoVariavel;
	}

	public void setImpactaRemuneracaoVariavel(Boolean impactaRemuneracaoVariavel) {
		this.impactaRemuneracaoVariavel = impactaRemuneracaoVariavel;
	}

	public TipoMeta getTipoMeta() {
		return tipoMeta;
	}

	public void setTipoMeta(TipoMeta tipoMeta) {
		this.tipoMeta = tipoMeta;
	}

	@XmlTransient
	public List<EspacoOcupacional> getEspacosOcupacionais() {
		return espacosOcupacionais;
	}

	public void setEspacosOcupacionais(List<EspacoOcupacional> espacosOcupacionais) {
		this.espacosOcupacionais = espacosOcupacionais;
	}

	@XmlTransient
	public List<Unidade> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	@XmlTransient
	public List<PorcentagemRemuneracaoVariavel> getPorcentagensRemuneracaoVariavel() {
		return porcentagensRemuneracaoVariavel;
	}

	public void setPorcentagensRemuneracaoVariavel(List<PorcentagemRemuneracaoVariavel> porcentagensRemuneracaoVariavel) {
		this.porcentagensRemuneracaoVariavel = porcentagensRemuneracaoVariavel;
	}

	public BigDecimal getRemuneracaoVariavel() {
		BigDecimal retorno = BigDecimal.ZERO;

		if (this.porcentagensRemuneracaoVariavel != null && !this.porcentagensRemuneracaoVariavel.isEmpty()) {
			BigDecimal somaPorcentagem = BigDecimal.ZERO;

			for (PorcentagemRemuneracaoVariavel prv : this.porcentagensRemuneracaoVariavel) {
				if (prv.getPercentualRemuneracaoVariavel() != null)
					somaPorcentagem = somaPorcentagem.add(prv.getPercentualRemuneracaoVariavel());
			}
			if (!somaPorcentagem.equals(BigDecimal.ZERO))
				retorno = somaPorcentagem.divide(BigDecimal.valueOf(this.porcentagensRemuneracaoVariavel.size()), 0,
						RoundingMode.HALF_UP);
		}
		return retorno;
	}

	public BigDecimal getPontuacaoMaximaFundamental() {
		return pontuacaoMaximaFundamental;
	}

	public void setPontuacaoMaximaFundamental(BigDecimal pontuacaoMaximaFundamental) {
		this.pontuacaoMaximaFundamental = pontuacaoMaximaFundamental;
	}

	public BigDecimal getPontuacaoMaximaComplementar() {
		return pontuacaoMaximaComplementar;
	}

	public void setPontuacaoMaximaComplementar(BigDecimal pontuacaoMaximaComplementar) {
		this.pontuacaoMaximaComplementar = pontuacaoMaximaComplementar;
	}

	public BigDecimal getCalculoHorasFundamental() {
		return calculoHorasFundamental;
	}

	public void setCalculoHorasFundamental(BigDecimal calculoHorasFundamental) {
		this.calculoHorasFundamental = calculoHorasFundamental;
	}

	public BigDecimal getCalculoHorasComplementar() {
		return calculoHorasComplementar;
	}

	public void setCalculoHorasComplementar(BigDecimal calculoHorasComplementar) {
		this.calculoHorasComplementar = calculoHorasComplementar;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ConfiguracaoMetas)) {
			return false;
		}
		ConfiguracaoMetas other = (ConfiguracaoMetas) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ConfiguracaoMetas[ id=" + id + " ]";
	}

}
