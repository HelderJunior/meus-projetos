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
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.TipoAcao;
import br.com.sebrae.sgm.model.enums.TipoExcecao;
import br.com.sebrae.sgm.model.enums.TipoMeta;

/**
 * @author Diego
 */
@Entity
@Table(name = "EXCECAO_RESPONSABILIDADES")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ExcecaoResponsabilidades.findAll", query = "SELECT e FROM ExcecaoResponsabilidades e"),
		@NamedQuery(name = "ExcecaoResponsabilidades.findById", query = "SELECT e FROM ExcecaoResponsabilidades e WHERE e.id = :id"),
		@NamedQuery(name = "ExcecaoResponsabilidades.findByTipoMeta", query = "SELECT e FROM ExcecaoResponsabilidades e WHERE e.tipoMeta = :tipoMeta"),
		@NamedQuery(name = "ExcecaoResponsabilidades.findByTipoAcao", query = "SELECT e FROM ExcecaoResponsabilidades e WHERE e.tipoAcao = :tipoAcao"),
		@NamedQuery(name = "ExcecaoResponsabilidades.findByTipoExcecao", query = "SELECT e FROM ExcecaoResponsabilidades e WHERE e.tipoExcecao = :tipoExcecao"),
		@NamedQuery(name = "ExcecaoResponsabilidades.findByInativarResponsavel", query = "SELECT e FROM ExcecaoResponsabilidades e WHERE e.inativarResponsavel = :inativarResponsavel") })
public class ExcecaoResponsabilidades implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "TIPO_META")
	@Enumerated(EnumType.STRING)
	private TipoMeta tipoMeta;

	@Column(name = "TIPO_ACAO")
	@Enumerated(EnumType.STRING)
	private TipoAcao tipoAcao;

	@Column(name = "TIPO_EXCECAO")
	@Enumerated(EnumType.STRING)
	private TipoExcecao tipoExcecao;

	@Basic(optional = false)
	@Column(name = "INATIVAR_RESPONSAVEL")
	private Boolean inativarResponsavel = Boolean.FALSE;

	@Basic(optional = false)
	@Column(name = "INATIVAR_RESPONSAVEL_ATUAL")
	private Boolean inativarResponsavelAtual = Boolean.FALSE;

	@Column(name = "VALIDAR_TEXTO_META")
	private Boolean validarTextoMeta = Boolean.FALSE;

	@Column(name = "VALIDAR_RESULTADO_META")
	private Boolean validarResultadoMeta = Boolean.FALSE;

	@JoinTable(name = "EXCECAO_USUARIO", joinColumns = { @JoinColumn(name = "ID_EXCECAO", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID") })
	@ManyToMany
	private List<Usuario> usuarios;

	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	@JoinColumns({
			@JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO_UNIDADE", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToOne(optional = true)
	private Unidade unidade;

	@JoinColumn(name = "ID_USUARIO_RESPONSAVEL", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Usuario usuarioResponsavel;

	@JoinTable(name = "EXCECAO_RESPONSABILIDADE_UNIDADE", joinColumns = { @JoinColumn(name = "ID_EXCECAO_RESPONSABILIDADE", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO_UNIDADE", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToMany
	private List<Unidade> unidades;

	public ExcecaoResponsabilidades() {
	}

	public ExcecaoResponsabilidades(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TipoMeta getTipoMeta() {
		return tipoMeta;
	}

	public void setTipoMeta(TipoMeta tipoMeta) {
		this.tipoMeta = tipoMeta;
	}

	public TipoAcao getTipoAcao() {
		return tipoAcao;
	}

	public void setTipoAcao(TipoAcao tipoAcao) {
		this.tipoAcao = tipoAcao;
	}

	public TipoExcecao getTipoExcecao() {
		return tipoExcecao;
	}

	public void setTipoExcecao(TipoExcecao tipoExcecao) {
		this.tipoExcecao = tipoExcecao;
	}

	public Boolean getInativarResponsavel() {
		return inativarResponsavel;
	}

	public void setInativarResponsavel(Boolean inativarResponsavel) {
		this.inativarResponsavel = inativarResponsavel;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public Usuario getUsuarioResponsavel() {
		return usuarioResponsavel;
	}

	public void setUsuarioResponsavel(Usuario usuarioResponsavel) {
		this.usuarioResponsavel = usuarioResponsavel;
	}

	public List<Unidade> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public Boolean getValidarTextoMeta() {
		return validarTextoMeta;
	}

	public void setValidarTextoMeta(Boolean validarTextoMeta) {
		this.validarTextoMeta = validarTextoMeta;
	}

	public Boolean getValidarResultadoMeta() {
		return validarResultadoMeta;
	}

	public void setValidarResultadoMeta(Boolean validarResultadoMeta) {
		this.validarResultadoMeta = validarResultadoMeta;
	}

	public Boolean getInativarResponsavelAtual() {
		return inativarResponsavelAtual;
	}

	public void setInativarResponsavelAtual(Boolean inativarResponsavelAtual) {
		this.inativarResponsavelAtual = inativarResponsavelAtual;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ExcecaoResponsabilidades)) {
			return false;
		}
		ExcecaoResponsabilidades other = (ExcecaoResponsabilidades) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.ExcecaoResponsabilidades[ id=" + id
				+ " ]";
	}

}
