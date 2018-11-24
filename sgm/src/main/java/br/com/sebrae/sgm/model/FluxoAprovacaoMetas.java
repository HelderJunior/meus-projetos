package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.FluxoCiclo;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "FLUXO_APROVACAO_METAS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "FluxoAprovacaoMetas.findAll", query = "SELECT f FROM FluxoAprovacaoMetas f"),
		@NamedQuery(name = "FluxoAprovacaoMetas.findById", query = "SELECT f FROM FluxoAprovacaoMetas f WHERE f.id = :id"),
		@NamedQuery(name = "FluxoAprovacaoMetas.findByNumeroFluxo", query = "SELECT f FROM FluxoAprovacaoMetas f WHERE f.fluxo = :numeroFluxo") })
public class FluxoAprovacaoMetas implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "NUMERO_FLUXO")
	private FluxoCiclo fluxo;

	@JoinTable(name = "FLUXO_UNIDADE", joinColumns = { @JoinColumn(name = "ID", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "UF", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToMany
	private List<Unidade> unidades;

	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	public FluxoAprovacaoMetas() {
	}

	public FluxoAprovacaoMetas(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FluxoCiclo getFluxo() {
		return fluxo;
	}

	public void setFluxo(FluxoCiclo fluxo) {
		this.fluxo = fluxo;
	}

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

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof FluxoAprovacaoMetas)) {
			return false;
		}
		FluxoAprovacaoMetas other = (FluxoAprovacaoMetas) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.FluxoAprovacaoMetas[ id=" + id + " ]";
	}

}
