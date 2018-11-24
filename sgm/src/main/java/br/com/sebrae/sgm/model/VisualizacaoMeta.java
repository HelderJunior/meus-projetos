package br.com.sebrae.sgm.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "VISUALIZACAO_META")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "VisualizacaoMeta.findAll", query = "SELECT v FROM VisualizacaoMeta v"),
		@NamedQuery(name = "VisualizacaoMeta.findById", query = "SELECT v FROM VisualizacaoMeta v WHERE v.id = :id"),
		@NamedQuery(name = "VisualizacaoMeta.findBySomenteSuasMetas", query = "SELECT v FROM VisualizacaoMeta v WHERE v.somenteSuasMetas = :somenteSuasMetas"),
		@NamedQuery(name = "VisualizacaoMeta.findByDemaisUnidades", query = "SELECT v FROM VisualizacaoMeta v WHERE v.demaisUnidades = :demaisUnidades"),
		@NamedQuery(name = "VisualizacaoMeta.findByIndivisuaisEquipePertencente", query = "SELECT v FROM VisualizacaoMeta v WHERE v.indivisuaisEquipePertencente = :indivisuaisEquipePertencente"),
		@NamedQuery(name = "VisualizacaoMeta.findByIndividuaisOutrasUnidades", query = "SELECT v FROM VisualizacaoMeta v WHERE v.individuaisOutrasUnidades = :individuaisOutrasUnidades"),
		@NamedQuery(name = "VisualizacaoMeta.findByIndividualUnidadesMestaDiretoria", query = "SELECT v FROM VisualizacaoMeta v WHERE v.individualUnidadesMestaDiretoria = :individualUnidadesMestaDiretoria"),
		@NamedQuery(name = "VisualizacaoMeta.findByIndividualTodos", query = "SELECT v FROM VisualizacaoMeta v WHERE v.individualTodos = :individualTodos"),
		@NamedQuery(name = "VisualizacaoMeta.findByEquipeDemaisUnidades", query = "SELECT v FROM VisualizacaoMeta v WHERE v.equipeDemaisUnidades = :equipeDemaisUnidades"),
		@NamedQuery(name = "VisualizacaoMeta.findByEquipeMestaDiretoria", query = "SELECT v FROM VisualizacaoMeta v WHERE v.equipeMestaDiretoria = :equipeMestaDiretoria"),
		@NamedQuery(name = "VisualizacaoMeta.findByEquipeTodos", query = "SELECT v FROM VisualizacaoMeta v WHERE v.equipeTodos = :equipeTodos") })
public class VisualizacaoMeta implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "SOMENTE_SUAS_METAS")
	private Boolean somenteSuasMetas = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "DEMAIS_UNIDADES")
	private Boolean demaisUnidades = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "INDIVISUAIS_EQUIPE_PERTENCENTE")
	private Boolean indivisuaisEquipePertencente = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "INDIVIDUAIS_OUTRAS_UNIDADES")
	private Boolean individuaisOutrasUnidades = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "INDIVIDUAL_UNIDADES_MESTA_DIRETORIA")
	private Boolean individualUnidadesMestaDiretoria = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "INDIVIDUAL_TODOS")
	private Boolean individualTodos = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "EQUIPE_DEMAIS_UNIDADES")
	private Boolean equipeDemaisUnidades = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "EQUIPE_MESTA_DIRETORIA")
	private Boolean equipeMestaDiretoria = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "EQUIPE_TODOS")
	private Boolean equipeTodos = Boolean.FALSE;

	@JoinColumn(name = "ID", referencedColumnName = "ID", insertable = false, updatable = false)
	@OneToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	public VisualizacaoMeta() {
	}

	public VisualizacaoMeta(Integer id) {
		this.id = id;
	}

	public VisualizacaoMeta(Integer id, Boolean somenteSuasMetas, Boolean demaisUnidades,
			Boolean indivisuaisEquipePertencente, Boolean individuaisOutrasUnidades,
			Boolean individualUnidadesMestaDiretoria, Boolean individualTodos, Boolean equipeDemaisUnidades,
			Boolean equipeMestaDiretoria, Boolean equipeTodos) {
		this.id = id;
		this.somenteSuasMetas = somenteSuasMetas;
		this.demaisUnidades = demaisUnidades;
		this.indivisuaisEquipePertencente = indivisuaisEquipePertencente;
		this.individuaisOutrasUnidades = individuaisOutrasUnidades;
		this.individualUnidadesMestaDiretoria = individualUnidadesMestaDiretoria;
		this.individualTodos = individualTodos;
		this.equipeDemaisUnidades = equipeDemaisUnidades;
		this.equipeMestaDiretoria = equipeMestaDiretoria;
		this.equipeTodos = equipeTodos;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getSomenteSuasMetas() {
		return somenteSuasMetas;
	}

	public void setSomenteSuasMetas(Boolean somenteSuasMetas) {
		this.somenteSuasMetas = somenteSuasMetas;
	}

	public Boolean getDemaisUnidades() {
		return demaisUnidades;
	}

	public void setDemaisUnidades(Boolean demaisUnidades) {
		this.demaisUnidades = demaisUnidades;
	}

	public Boolean getIndivisuaisEquipePertencente() {
		return indivisuaisEquipePertencente;
	}

	public void setIndivisuaisEquipePertencente(Boolean indivisuaisEquipePertencente) {
		this.indivisuaisEquipePertencente = indivisuaisEquipePertencente;
	}

	public Boolean getIndividuaisOutrasUnidades() {
		return individuaisOutrasUnidades;
	}

	public void setIndividuaisOutrasUnidades(Boolean individuaisOutrasUnidades) {
		this.individuaisOutrasUnidades = individuaisOutrasUnidades;
	}

	public Boolean getIndividualUnidadesMestaDiretoria() {
		return individualUnidadesMestaDiretoria;
	}

	public void setIndividualUnidadesMestaDiretoria(Boolean individualUnidadesMestaDiretoria) {
		this.individualUnidadesMestaDiretoria = individualUnidadesMestaDiretoria;
	}

	public Boolean getIndividualTodos() {
		return individualTodos;
	}

	public void setIndividualTodos(Boolean individualTodos) {
		this.individualTodos = individualTodos;
	}

	public Boolean getEquipeDemaisUnidades() {
		return equipeDemaisUnidades;
	}

	public void setEquipeDemaisUnidades(Boolean equipeDemaisUnidades) {
		this.equipeDemaisUnidades = equipeDemaisUnidades;
	}

	public Boolean getEquipeMestaDiretoria() {
		return equipeMestaDiretoria;
	}

	public void setEquipeMestaDiretoria(Boolean equipeMestaDiretoria) {
		this.equipeMestaDiretoria = equipeMestaDiretoria;
	}

	public Boolean getEquipeTodos() {
		return equipeTodos;
	}

	public void setEquipeTodos(Boolean equipeTodos) {
		this.equipeTodos = equipeTodos;
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
		if (!(object instanceof VisualizacaoMeta)) {
			return false;
		}

		VisualizacaoMeta other = (VisualizacaoMeta) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.VisualizacaoMeta[ id=" + id + " ]";
	}

}
