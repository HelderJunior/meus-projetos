package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "RM_Competencia")
@XmlRootElement
public class RMCompetencia implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected RMCompetenciaPK rmCompetenciaPK;

	@Column(name = "CompetenciaAno")
	private Integer competenciaAno;

	@Column(name = "CompetenciaDescricao")
	private String competenciaDescricao;

	@Basic(optional = false)
	@Column(name = "CompetenciaSituacao")
	private Character competenciaSituacao;

	@Column(name = "CompetenciaNivelProficiencia")
	private String competenciaNivelProficiencia;

	@JoinColumns({
			@JoinColumn(name = "ColaboradorCPF", referencedColumnName = "ColaboradorCpf", insertable = false, updatable = false),
			@JoinColumn(name = "ColaboradorMatricula", referencedColumnName = "ColaboradorMatricula", insertable = false, updatable = false),
			@JoinColumn(name = "UnidadeUF", referencedColumnName = "UnidadeUf", insertable = false, updatable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private RMColaborador rmColaborador;

	@ManyToMany(mappedBy = "competencias")
	private List<Meta> metas;

	@Transient
	private Boolean selecionado = Boolean.FALSE;

	public RMCompetencia() {
	}

	public RMCompetenciaPK getRmCompetenciaPK() {
		return rmCompetenciaPK;
	}

	public void setRmCompetenciaPK(RMCompetenciaPK rmCompetenciaPK) {
		this.rmCompetenciaPK = rmCompetenciaPK;
	}

	public Integer getCompetenciaAno() {
		return competenciaAno;
	}

	public void setCompetenciaAno(Integer competenciaAno) {
		this.competenciaAno = competenciaAno;
	}

	public String getCompetenciaDescricao() {
		return competenciaDescricao;
	}

	public void setCompetenciaDescricao(String competenciaDescricao) {
		this.competenciaDescricao = competenciaDescricao;
	}

	public Character getCompetenciaSituacao() {
		return competenciaSituacao;
	}

	public void setCompetenciaSituacao(Character competenciaSituacao) {
		this.competenciaSituacao = competenciaSituacao;
	}

	public String getCompetenciaNivelProficiencia() {
		return competenciaNivelProficiencia;
	}

	public void setCompetenciaNivelProficiencia(String competenciaNivelProficiencia) {
		this.competenciaNivelProficiencia = competenciaNivelProficiencia;
	}

	public RMColaborador getRmColaborador() {
		return rmColaborador;
	}

	public void setRmColaborador(RMColaborador rmColaborador) {
		this.rmColaborador = rmColaborador;
	}

	public List<Meta> getMetas() {
		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rmCompetenciaPK == null) ? 0 : rmCompetenciaPK.hashCode());
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
		RMCompetencia other = (RMCompetencia) obj;
		if (rmCompetenciaPK == null) {
			if (other.rmCompetenciaPK != null)
				return false;
		} else if (!rmCompetenciaPK.equals(other.rmCompetenciaPK))
			return false;
		return true;
	}

}
