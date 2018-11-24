package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.Abrangencia;
import br.com.sebrae.sgm.model.enums.AtivoInativo;
import br.com.sebrae.sgm.model.enums.TipoCargaHoraria;
import br.com.sebrae.sgm.model.enums.UF;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "FORMA_AQUISICAO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "FormaAquisicao.findAll", query = "SELECT fa FROM FormaAquisicao fa"),
		@NamedQuery(name = "FormaAquisicao.findAllAtivas", query = "SELECT fa FROM FormaAquisicao fa where fa.status = 'A'") })
public class FormaAquisicao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "NOME")
	private String nome;

	@Column(name = "DESCRICAO")
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private AtivoInativo status;

	@Enumerated(EnumType.STRING)
	@Column(name = "ABRANGENCIA")
	private Abrangencia abrangencia;

	@Enumerated(EnumType.STRING)
	@Column(name = "UF")
	private UF uf;

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_CARGA_HORARIA")
	private TipoCargaHoraria tipoCargaHoraria;

	@Column(name = "UNIDADE_MEDIDA")
	private String unidadeMedida;

	@Column(name = "CARGA_HORARIA")
	private BigDecimal cargaHoraria;

	@JoinColumn(name = "ID_CATEGORIA", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Categoria categoria;

	@JoinColumn(name = "ID_VALIDACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Validacao validacao;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formaAquisicao")
	private List<SolucaoEducacional> solucoesEducacional;

	@Transient
	private Boolean selecionado;

	public FormaAquisicao() {
	}

	public FormaAquisicao(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Abrangencia getAbrangencia() {
		return abrangencia;
	}

	public void setAbrangencia(Abrangencia abrangencia) {
		this.abrangencia = abrangencia;
	}

	public AtivoInativo getStatus() {
		return status;
	}

	public void setStatus(AtivoInativo status) {
		this.status = status;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public TipoCargaHoraria getTipoCargaHoraria() {
		return tipoCargaHoraria;
	}

	public void setTipoCargaHoraria(TipoCargaHoraria tipoCargaHoraria) {
		this.tipoCargaHoraria = tipoCargaHoraria;
	}

	public String getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public BigDecimal getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(BigDecimal cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Validacao getValidacao() {
		return validacao;
	}

	public void setValidacao(Validacao validacao) {
		this.validacao = validacao;
	}

	public List<SolucaoEducacional> getSolucoesEducacional() {
		return solucoesEducacional;
	}

	public void setSolucoesEducacional(List<SolucaoEducacional> solucoesEducacional) {
		this.solucoesEducacional = solucoesEducacional;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof FormaAquisicao)) {
			return false;
		}
		FormaAquisicao other = (FormaAquisicao) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

}
