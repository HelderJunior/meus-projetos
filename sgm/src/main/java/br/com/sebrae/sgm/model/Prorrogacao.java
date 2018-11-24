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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.Fase;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "PRORROGACAO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Prorrogacao.findAll", query = "SELECT p FROM Prorrogacao p"),
		@NamedQuery(name = "Prorrogacao.findById", query = "SELECT p FROM Prorrogacao p WHERE p.id = :id"),
		@NamedQuery(name = "Prorrogacao.findByDtInicio", query = "SELECT p FROM Prorrogacao p WHERE p.dtInicio = :dtInicio"),
		@NamedQuery(name = "Prorrogacao.findByDtFim", query = "SELECT p FROM Prorrogacao p WHERE p.dtFim = :dtFim") })
public class Prorrogacao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "DT_INICIO")
	private Date dtInicio;

	@Basic(optional = false)
	@Column(name = "DT_FIM")
	private Date dtFim;

	@Enumerated(EnumType.STRING)
	@Column(name = "FASE")
	private Fase fase;

	@JoinColumn(name = "ID_CALENDARIO", referencedColumnName = "ID")
	@ManyToOne
	private Calendario calendario;

	@JoinColumns({ @JoinColumn(name = "UF", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToOne
	private Unidade unidade;

	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")
	@ManyToOne
	private Usuario usuario;

	public Prorrogacao() {
	}

	public Prorrogacao(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Calendario getCalendario() {
		return calendario;
	}

	public void setCalendario(Calendario calendario) {
		this.calendario = calendario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Fase getFase() {
		return fase;
	}

	public void setFase(Fase fase) {
		this.fase = fase;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Prorrogacao)) {
			return false;
		}
		Prorrogacao other = (Prorrogacao) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Prorrogacao[ id=" + id + " ]";
	}

}
