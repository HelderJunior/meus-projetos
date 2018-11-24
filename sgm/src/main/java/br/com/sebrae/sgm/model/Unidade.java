package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.UF;

/**
 * @author Diego
 */
@Entity
@Table(name = "RM_Unidade")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Unidade.findAll", query = "SELECT u FROM Unidade u where u.ativo = true order by u.descricao asc"),
		@NamedQuery(name = "Unidade.findByUf", query = "SELECT u FROM Unidade u WHERE u.unidadePK.uf = :uf and u.ativo = true"),
		@NamedQuery(name = "Unidade.findByCodigo", query = "SELECT u FROM Unidade u WHERE u.unidadePK.codigo = :codigo"),
		@NamedQuery(name = "Unidade.findByDescricao", query = "SELECT u FROM Unidade u WHERE u.descricao = :descricao"),
		@NamedQuery(name = "Unidade.findByUfUsuario", query = "SELECT u FROM Unidade u join u.usuarios usu WHERE u.unidadePK.uf = :uf and usu.id = :idUsuario") })
public class Unidade implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected UnidadePK unidadePK;

	@Column(name = "UnidadeDescricao")
	private String descricao;

	@Column(name = "UnidadeCodDiretoria")
	private String codigoDiretoria;

	@Column(name = "UnidadeDescDiretoria")
	private String descricaoDiretoria;

	@Column(name = "UnidadeSituacao")
	private Boolean ativo;

	@OneToMany(mappedBy = "unidade", fetch = FetchType.LAZY)
	private List<Usuario> usuarios;

	@ManyToMany(mappedBy = "unidadesVinculadas")
	private List<Meta> metasUnidadesVinculadas;

	@OneToMany(mappedBy = "unidade")
	private List<Meta> metas;

	@OneToMany(mappedBy = "unidade")
	private List<UsuarioPerfil> usuarioPerfilList;

	@OneToMany(mappedBy = "rmUnidade", fetch = FetchType.LAZY)
	private List<RMLogUnidade> rmLogUnidadeList;

	@OneToMany(mappedBy = "rmUnidade", fetch = FetchType.LAZY)
	private List<RMColaborador> rmColaboradorList;

	@Transient
	private Boolean showDetalhes = Boolean.FALSE;

	@Transient
	private AvaliadorCiclo gerenteTemp;

	@Transient
	private AvaliadorCiclo gerenteAdjuntoTemp;

	@Transient
	private AvaliadorCiclo diretorTemp;

	@Transient
	private AvaliadorCiclo assessorTemp;

	@Transient
	private AvaliadorCiclo chefeGabineteTemp;

	@Transient
	private AvaliadorCiclo auditorTemp;

	@Transient
	private AvaliadorCiclo comiteTemp;

	@Transient
	private AvaliadorCiclo avaliadorExternoTemp;

	public Unidade() {
	}

	public Unidade(UnidadePK unidadePK) {
		this.unidadePK = unidadePK;
	}

	public Unidade(UF uf, String codigo) {
		this.unidadePK = new UnidadePK(uf, codigo);
	}

	public UnidadePK getUnidadePK() {
		return unidadePK;
	}

	public void setUnidadePK(UnidadePK unidadePK) {
		this.unidadePK = unidadePK;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getDescricao(int tamanhoMax) {

		if (descricao != null && descricao.length() > tamanhoMax) {
			return descricao.substring(0, tamanhoMax);
		}
		return descricao;
	}

	public String getDescricaoComCodigo() {
		return this.unidadePK.getCodigo() + " - " + this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Meta> getMetasUnidadesVinculadas() {
		return metasUnidadesVinculadas;
	}

	public void setMetasUnidadesVinculadas(List<Meta> metasUnidadesVinculadas) {
		this.metasUnidadesVinculadas = metasUnidadesVinculadas;
	}

	public List<Meta> getMetas() {
		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public String getCodigoDiretoria() {
		return codigoDiretoria;
	}

	public void setCodigoDiretoria(String codigoDiretoria) {
		this.codigoDiretoria = codigoDiretoria;
	}

	public String getDescricaoDiretoria() {
		return descricaoDiretoria;
	}

	public void setDescricaoDiretoria(String descricaoDiretoria) {
		this.descricaoDiretoria = descricaoDiretoria;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public List<UsuarioPerfil> getUsuarioPerfilList() {
		return usuarioPerfilList;
	}

	public void setUsuarioPerfilList(List<UsuarioPerfil> usuarioPerfilList) {
		this.usuarioPerfilList = usuarioPerfilList;
	}

	public AvaliadorCiclo getGerenteTemp() {
		return gerenteTemp;
	}

	public void setGerenteTemp(AvaliadorCiclo gerenteTemp) {
		this.gerenteTemp = gerenteTemp;
	}

	public AvaliadorCiclo getGerenteAdjuntoTemp() {
		return gerenteAdjuntoTemp;
	}

	public void setGerenteAdjuntoTemp(AvaliadorCiclo gerenteAdjuntoTemp) {
		this.gerenteAdjuntoTemp = gerenteAdjuntoTemp;
	}

	public AvaliadorCiclo getDiretorTemp() {
		return diretorTemp;
	}

	public void setDiretorTemp(AvaliadorCiclo diretorTemp) {
		this.diretorTemp = diretorTemp;
	}

	public AvaliadorCiclo getAssessorTemp() {
		return assessorTemp;
	}

	public void setAssessorTemp(AvaliadorCiclo assessorTemp) {
		this.assessorTemp = assessorTemp;
	}

	public AvaliadorCiclo getChefeGabineteTemp() {
		return chefeGabineteTemp;
	}

	public void setChefeGabineteTemp(AvaliadorCiclo chefeGabineteTemp) {
		this.chefeGabineteTemp = chefeGabineteTemp;
	}

	public AvaliadorCiclo getAuditorTemp() {
		return auditorTemp;
	}

	public void setAuditorTemp(AvaliadorCiclo auditorTemp) {
		this.auditorTemp = auditorTemp;
	}

	public AvaliadorCiclo getComiteTemp() {
		return comiteTemp;
	}

	public void setComiteTemp(AvaliadorCiclo comiteTemp) {
		this.comiteTemp = comiteTemp;
	}

	public AvaliadorCiclo getAvaliadorExternoTemp() {
		return avaliadorExternoTemp;
	}

	public void setAvaliadorExternoTemp(AvaliadorCiclo avaliadorExternoTemp) {
		this.avaliadorExternoTemp = avaliadorExternoTemp;
	}

	public Boolean getShowDetalhes() {
		return showDetalhes;
	}

	public void setShowDetalhes(Boolean showDetalhes) {
		this.showDetalhes = showDetalhes;
	}

	public List<RMLogUnidade> getRmLogUnidadeList() {
		return rmLogUnidadeList;
	}

	public void setRmLogUnidadeList(List<RMLogUnidade> rmLogUnidadeList) {
		this.rmLogUnidadeList = rmLogUnidadeList;
	}

	public List<RMColaborador> getRmColaboradorList() {
		return rmColaboradorList;
	}

	public void setRmColaboradorList(List<RMColaborador> rmColaboradorList) {
		this.rmColaboradorList = rmColaboradorList;
	}

	public String getNomesCargosTemp() {
		StringBuilder retorno = new StringBuilder();

		if (this.gerenteTemp != null) {
			retorno.append(this.gerenteTemp.getPerfil().getDescricao() + ": " + this.gerenteTemp.getUsuario().getNome()
					+ "<br> ");
		}

		if (this.gerenteAdjuntoTemp != null) {
			retorno.append(this.gerenteAdjuntoTemp.getPerfil().getDescricao() + ": "
					+ this.gerenteAdjuntoTemp.getUsuario().getNome() + "<br> ");
		}

		if (this.diretorTemp != null) {
			retorno.append(this.diretorTemp.getPerfil().getDescricao() + ": " + this.diretorTemp.getUsuario().getNome()
					+ "<br> ");
		}

		if (this.assessorTemp != null) {
			retorno.append(this.assessorTemp.getPerfil().getDescricao() + ": "
					+ this.assessorTemp.getUsuario().getNome() + "<br> ");
		}

		if (this.chefeGabineteTemp != null) {
			retorno.append(this.chefeGabineteTemp.getPerfil().getDescricao() + ": "
					+ this.chefeGabineteTemp.getUsuario().getNome() + "<br> ");
		}

		return retorno.toString();
	}

	public String getNomesCargosExternosTemp() {
		StringBuilder retorno = new StringBuilder();

		if (this.comiteTemp != null) {
			retorno.append(this.comiteTemp.getPerfil().getDescricao() + ": " + this.comiteTemp.getUsuario().getNome()
					+ "<br> ");
		}

		if (this.auditorTemp != null) {
			retorno.append(this.auditorTemp.getPerfil().getDescricao() + ": " + this.auditorTemp.getUsuario().getNome()
					+ "<br> ");
		}

		if (this.avaliadorExternoTemp != null) {
			retorno.append(this.avaliadorExternoTemp.getPerfil().getDescricao() + ": "
					+ this.avaliadorExternoTemp.getUsuario().getNome() + "<br> ");
		}

		return retorno.toString();
	}

	public boolean isPossuiAvaliador() {
		if (this.gerenteTemp != null || this.gerenteAdjuntoTemp != null || this.diretorTemp != null
				|| this.assessorTemp != null || this.chefeGabineteTemp != null) {
			return true;
		}
		return false;
	}

	public boolean isPossuiAvaliadorExterno() {
		if (this.comiteTemp != null || this.auditorTemp != null || this.avaliadorExternoTemp != null) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (unidadePK != null ? unidadePK.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Unidade)) {
			return false;
		}
		Unidade other = (Unidade) object;
		if ((this.getUnidadePK() == null && other.getUnidadePK() != null)
				|| (this.getUnidadePK() != null && !this.getUnidadePK().equals(other.getUnidadePK()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getDescricaoComCodigo();
	}

}
