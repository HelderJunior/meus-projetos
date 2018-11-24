package br.com.sebrae.sgm.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * 
 * @author 98379720172
 */
@Entity
@Table(name = "EXPORTACAO_DETALHE")
@NamedQueries({
		@NamedQuery(name = "ExportacaoDetalhe.findAll", query = "SELECT e FROM ExportacaoDetalhe e"),
		@NamedQuery(name = "ExportacaoDetalhe.findById", query = "SELECT e FROM ExportacaoDetalhe e WHERE e.id = :id"),
		@NamedQuery(name = "ExportacaoDetalhe.findByMetasEstipuladas", query = "SELECT e FROM ExportacaoDetalhe e WHERE e.metasEstipuladas = :metasEstipuladas"),
		@NamedQuery(name = "ExportacaoDetalhe.findByMetasAlcancadas", query = "SELECT e FROM ExportacaoDetalhe e WHERE e.metasAlcancadas = :metasAlcancadas"),
		@NamedQuery(name = "ExportacaoDetalhe.findByAnoVigencia", query = "SELECT e FROM ExportacaoDetalhe e WHERE e.anoVigencia = :anoVigencia") })
public class ExportacaoDetalhe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "METAS_ESTIPULADAS")
	private int metasEstipuladas;

	@Basic(optional = false)
	@Column(name = "METAS_ALCANCADAS")
	private int metasAlcancadas;

	@Basic(optional = false)
	@Column(name = "ANO_VIGENCIA")
	private int anoVigencia;

	@JoinColumn(name = "ID_EXPORTACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Exportacao exportacao;

	@JoinColumns({ @JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO_UNIDADE", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Unidade unidade;

	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Usuario usuario;

	public ExportacaoDetalhe() {
	}

	public ExportacaoDetalhe(Integer id) {
		this.id = id;
	}

	public ExportacaoDetalhe(Integer id, int metasEstipuladas, int metasAlcancadas, int anoVigencia) {
		this.id = id;
		this.metasEstipuladas = metasEstipuladas;
		this.metasAlcancadas = metasAlcancadas;
		this.anoVigencia = anoVigencia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getMetasEstipuladas() {
		return metasEstipuladas;
	}

	public void setMetasEstipuladas(int metasEstipuladas) {
		this.metasEstipuladas = metasEstipuladas;
	}

	public int getMetasAlcancadas() {
		return metasAlcancadas;
	}

	public void setMetasAlcancadas(int metasAlcancadas) {
		this.metasAlcancadas = metasAlcancadas;
	}

	public int getAnoVigencia() {
		return anoVigencia;
	}

	public void setAnoVigencia(int anoVigencia) {
		this.anoVigencia = anoVigencia;
	}

	public Exportacao getExportacao() {
		return exportacao;
	}

	public void setExportacao(Exportacao exportacao) {
		this.exportacao = exportacao;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ExportacaoDetalhe)) {
			return false;
		}
		ExportacaoDetalhe other = (ExportacaoDetalhe) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ExportacaoDetalhe[ id=" + id + " ]";
	}

}
