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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusCiclo;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "FASE_CICLO")
@XmlRootElement
public class FaseCiclo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@JoinColumn(name = "ID_FASE_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private FaseCicloConfiguracao faseCicloConfiguracao;

	@Column(name = "FASE")
	@Enumerated(EnumType.STRING)
	private Fase fase;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private StatusCiclo status;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_INICIO")
	private Date dataInicio;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_TERMINO")
	private Date dataTermino;

	public FaseCiclo() {
	}

	public FaseCiclo(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FaseCicloConfiguracao getFaseCicloConfiguracao() {
		return faseCicloConfiguracao;
	}

	public void setFaseCicloConfiguracao(FaseCicloConfiguracao faseCicloConfiguracao) {
		this.faseCicloConfiguracao = faseCicloConfiguracao;
	}

	public Fase getFase() {
		return fase;
	}

	public void setFase(Fase fase) {
		this.fase = fase;
	}

	public StatusCiclo getStatus() {
		return status;
	}

	public void setStatus(StatusCiclo status) {
		this.status = status;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataTermino() {
		return dataTermino;
	}

	public void setDataTermino(Date dataTermino) {
		this.dataTermino = dataTermino;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof FaseCiclo)) {
			return false;
		}
		FaseCiclo other = (FaseCiclo) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

}
