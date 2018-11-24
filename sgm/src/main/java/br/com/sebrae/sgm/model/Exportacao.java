package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author 98379720172
 */
@Entity
@Table(name = "EXPORTACAO")
@NamedQueries({
		@NamedQuery(name = "Exportacao.findAll", query = "SELECT e FROM Exportacao e"),
		@NamedQuery(name = "Exportacao.findById", query = "SELECT e FROM Exportacao e WHERE e.id = :id"),
		@NamedQuery(name = "Exportacao.findByDtHoraExportacao", query = "SELECT e FROM Exportacao e WHERE e.dtHoraExportacao = :dtHoraExportacao") })
public class Exportacao implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "DT_HORA_EXPORTACAO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dtHoraExportacao = new Date();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "exportacao", fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ExportacaoDetalhe> detalhesExportacao;

	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private CicloConfiguracao cicloConfiguracao;

	@JoinColumn(name = "ID_USUARIO_EXPORTADOR", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Usuario usuarioExportador;

	public Exportacao() {
	}

	public Exportacao(Integer id) {
		this.id = id;
	}

	public Exportacao(Integer id, Date dtHoraExportacao) {
		this.id = id;
		this.dtHoraExportacao = dtHoraExportacao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDtHoraExportacao() {
		return dtHoraExportacao;
	}

	public void setDtHoraExportacao(Date dtHoraExportacao) {
		this.dtHoraExportacao = dtHoraExportacao;
	}

	public List<ExportacaoDetalhe> getDetalhesExportacao() {
		return detalhesExportacao;
	}

	public void setDetalhesExportacao(List<ExportacaoDetalhe> detalhesExportacao) {
		this.detalhesExportacao = detalhesExportacao;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public Usuario getUsuarioExportador() {
		return usuarioExportador;
	}

	public void setUsuarioExportador(Usuario usuarioExportador) {
		this.usuarioExportador = usuarioExportador;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Exportacao)) {
			return false;
		}
		Exportacao other = (Exportacao) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Exportacao[ id=" + id + " ]";
	}

}
