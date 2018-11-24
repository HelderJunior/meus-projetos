package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.TipoGrupo;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "FASE_CICLO_CONFIGURACAO")
@XmlRootElement
public class FaseCicloConfiguracao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO")
	private TipoGrupo tipo;

	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "faseCicloConfiguracao", orphanRemoval = true)
	private List<FaseCiclo> fases;

	public FaseCicloConfiguracao() {
	}

	public FaseCicloConfiguracao(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TipoGrupo getTipo() {
		return tipo;
	}

	public void setTipo(TipoGrupo tipo) {
		this.tipo = tipo;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public List<FaseCiclo> getFases() {
		return fases;
	}

	public void setFases(List<FaseCiclo> fases) {
		this.fases = fases;
	}

	public FaseCiclo getFaseByTipo(Fase fase) {
		if (this.fases != null) {
			for (FaseCiclo fc : this.fases) {
				if (fc.getFase() == fase)
					return fc;
			}
		}
		return null;
	}

	public void adicionarFase(FaseCiclo faseCiclo) {
		if (this.fases == null) {
			this.fases = new ArrayList<FaseCiclo>();
		}
		this.fases.add(faseCiclo);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof FaseCicloConfiguracao)) {
			return false;
		}
		FaseCicloConfiguracao other = (FaseCicloConfiguracao) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

}
