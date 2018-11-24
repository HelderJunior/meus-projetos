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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.TipoMeta;

/**
 * @author Diego
 */
@Entity
@Table(name = "CONFIGURACAO_MARCO_CRITICO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ConfiguracaoMarcoCritico.findAll", query = "SELECT c FROM ConfiguracaoMarcoCritico c"),
		@NamedQuery(name = "ConfiguracaoMarcoCritico.findById", query = "SELECT c FROM ConfiguracaoMarcoCritico c WHERE c.id = :id"),
		@NamedQuery(name = "ConfiguracaoMarcoCritico.findByDisponibilizadoInsercaoMetas", query = "SELECT c FROM ConfiguracaoMarcoCritico c WHERE c.disponibilizadoInsercaoMetas = :disponibilizadoInsercaoMetas"),
		@NamedQuery(name = "ConfiguracaoMarcoCritico.findByInsercaoObrigatoria", query = "SELECT c FROM ConfiguracaoMarcoCritico c WHERE c.insercaoObrigatoria = :insercaoObrigatoria"),
		@NamedQuery(name = "ConfiguracaoMarcoCritico.findByQtdMarcosCriticos", query = "SELECT c FROM ConfiguracaoMarcoCritico c WHERE c.qtdMarcosCriticos = :qtdMarcosCriticos") })
public class ConfiguracaoMarcoCritico implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "DISPONIBILIZADO_INSERCAO_METAS")
	private Boolean disponibilizadoInsercaoMetas = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "INSERCAO_OBRIGATORIA")
	private Boolean insercaoObrigatoria = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "QTD_MARCOS_CRITICOS")
	private Integer qtdMarcosCriticos = 0;

	@Column(name = "TIPO_META")
	@Enumerated(EnumType.STRING)
	private TipoMeta tipo;

	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	@JoinColumns({ @JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "COD_UNIDADE", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToOne
	private Unidade unidade;

	public ConfiguracaoMarcoCritico() {
	}

	public ConfiguracaoMarcoCritico(Integer id) {
		this.id = id;
	}

	public ConfiguracaoMarcoCritico(Integer id, Boolean disponibilizadoInsercaoMetas, Boolean insercaoObrigatoria,
			Integer qtdMarcosCriticos) {
		this.id = id;
		this.disponibilizadoInsercaoMetas = disponibilizadoInsercaoMetas;
		this.insercaoObrigatoria = insercaoObrigatoria;
		this.qtdMarcosCriticos = qtdMarcosCriticos;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getDisponibilizadoInsercaoMetas() {
		return disponibilizadoInsercaoMetas;
	}

	public void setDisponibilizadoInsercaoMetas(Boolean disponibilizadoInsercaoMetas) {
		this.disponibilizadoInsercaoMetas = disponibilizadoInsercaoMetas;
	}

	public Boolean getInsercaoObrigatoria() {
		return insercaoObrigatoria;
	}

	public void setInsercaoObrigatoria(Boolean insercaoObrigatoria) {
		this.insercaoObrigatoria = insercaoObrigatoria;
	}

	public Integer getQtdMarcosCriticos() {
		return qtdMarcosCriticos;
	}

	public void setQtdMarcosCriticos(Integer qtdMarcosCriticos) {
		this.qtdMarcosCriticos = qtdMarcosCriticos;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public TipoMeta getTipo() {
		return tipo;
	}

	public void setTipo(TipoMeta tipo) {
		this.tipo = tipo;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ConfiguracaoMarcoCritico)) {
			return false;
		}
		ConfiguracaoMarcoCritico other = (ConfiguracaoMarcoCritico) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ConfiguracaoMarcoCritico[ id=" + id + " ]";
	}

}
