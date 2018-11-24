package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.dto.MetaDesenvolvimentoComiteDTO;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Categoria;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.FormaAquisicao;
import br.com.sebrae.sgm.model.PropriedadesUsuarioExterno;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusSolucaoEducacional;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.model.enums.VinculoOcupacional;
import br.com.sebrae.sgm.service.CategoriaService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.FormaAquisicaoService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ComiteGestaoMetasDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ComiteGestaoMetasDesenvolvimentoBean.class);

	@Inject
	private CicloService cicloService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private CategoriaService categoriaService;

	@Inject
	private FormaAquisicaoService formaAquisicaoService;

	@Inject
	private MetaService metaService;

	@Inject
	private AppBean appBean;

	private Ciclo ciclo;
	private List<Ciclo> ciclos = new ArrayList<Ciclo>();
	private Unidade unidade;
	private List<Unidade> unidades;
	private Fase faseCiclo;
	private Usuario colaborador;
	private Categoria categoria;
	private List<Categoria> categorias;
	private FormaAquisicao formaAquisicao;
	private VinculoOcupacional vinculoOcupacional;
	private StatusSolucaoEducacional statusSolucao;

	private List<MetaDesenvolvimentoComiteDTO> metasGerencia = new ArrayList<MetaDesenvolvimentoComiteDTO>();

	private UF uf;

	public void pesquisar() {
		try {
			validarPesquisa();
			this.metasGerencia = this.metaService.findUsuariosComMetaDesenvolvimentoValidarComite(this.ciclo,
					this.unidade, this.faseCiclo, this.colaborador, this.categoria, this.formaAquisicao,
					this.vinculoOcupacional, this.statusSolucao, this.uf);
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void validarPesquisa() throws ValidateException {
		List<String> msgErros = new ArrayList<String>();

		if (!informouAlgumFiltro()) {
			msgErros.add("\u00C9 necess\u00E1rio informar no m\u00EDnimo um par\u00E2metro para consulta.");
		}

		if (!msgErros.isEmpty()) {
			throw new ValidateException(msgErros);
		}
	}

	private boolean informouAlgumFiltro() {
		if (ciclo != null || unidade != null || faseCiclo != null || this.colaborador != null || this.categoria != null
				|| this.formaAquisicao != null || this.vinculoOcupacional != null || this.statusSolucao != null
				|| this.uf != null) {
			return true;
		}
		return false;
	}

	public void alterouUnidade() {
		this.colaborador = null;
	}

	public void alterouCategoria() {
		this.formaAquisicao = null;
	}

	public List<Ciclo> getCiclos() {
		if (ciclos == null || ciclos.isEmpty()) {
			ciclos = cicloService.findAll();
		}
		return ciclos;
	}

	public void setCiclos(List<Ciclo> ciclos) {
		this.ciclos = ciclos;
	}

	public List<Unidade> getUnidades() {
		if (unidades == null || unidades.isEmpty()) {
			unidades = unidadeService.findAll();
		}
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public Ciclo getCiclo() {
		return ciclo;
	}

	public void setCiclo(Ciclo ciclo) {
		this.ciclo = ciclo;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Fase getFaseCiclo() {
		return faseCiclo;
	}

	public void setFaseCiclo(Fase faseCiclo) {
		this.faseCiclo = faseCiclo;
	}

	public List<MetaDesenvolvimentoComiteDTO> getMetasGerencia() {
		return metasGerencia;
	}

	public void setMetasGerencia(List<MetaDesenvolvimentoComiteDTO> metasGerencia) {
		this.metasGerencia = metasGerencia;
	}

	public List<Fase> getFasesCiclo() {
		return Arrays.asList(Fase.values());
	}

	public Usuario getColaborador() {
		return colaborador;
	}

	public void setColaborador(Usuario colaborador) {
		this.colaborador = colaborador;
	}

	public List<Usuario> getColaboradores() {
		List<Usuario> retorno = new ArrayList<Usuario>();
		if (this.unidade != null) {
			retorno = this.usuarioService.findByUnidade(this.unidade);
		}
		return retorno;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public List<Categoria> getCategorias() {
		if (categorias == null || categorias.isEmpty()) {
			categorias = categoriaService.findAll();
		}
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public FormaAquisicao getFormaAquisicao() {
		return formaAquisicao;
	}

	public void setFormaAquisicao(FormaAquisicao formaAquisicao) {
		this.formaAquisicao = formaAquisicao;
	}

	public List<FormaAquisicao> getFormasAquisicao() {
		List<FormaAquisicao> retorno = new ArrayList<FormaAquisicao>();
		if (this.categoria != null) {
			retorno = this.formaAquisicaoService.findByCategoria(this.categoria);
		}
		return retorno;
	}

	public VinculoOcupacional getVinculoOcupacional() {
		return vinculoOcupacional;
	}

	public void setVinculoOcupacional(VinculoOcupacional vinculoOcupacional) {
		this.vinculoOcupacional = vinculoOcupacional;
	}

	public StatusSolucaoEducacional getStatusSolucao() {
		return statusSolucao;
	}

	public void setStatusSolucao(StatusSolucaoEducacional statusSolucao) {
		this.statusSolucao = statusSolucao;
	}

	public List<VinculoOcupacional> getVinculosOcupacionais() {
		return Arrays.asList(VinculoOcupacional.values());
	}

	public List<StatusSolucaoEducacional> getStatusSolucaoEducacional() {
		return Arrays.asList(StatusSolucaoEducacional.values());
	}

	public List<Unidade> completeUnidade(String query) {
		if (this.uf != null) {
			List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUF(query, this.uf);
			return retorno;
		} else {
			List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUFs(query, getUfs());
			return retorno;
		}
	}

	public List<Usuario> completeUsuariosInterno(String nome) {
		List<Usuario> usuarioInterno = usuarioService.findByNameLikeAndUnidade(nome, this.unidade);
		return usuarioInterno;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public List<UF> getUfs() {
		Set<UF> retorno = new HashSet<UF>();

		List<PropriedadesUsuarioExterno> propriedadesUsuarioExterno = appBean.getUsuarioAutenticado()
				.getPropriedadesUsuarioExterno();
		if (propriedadesUsuarioExterno != null && !propriedadesUsuarioExterno.isEmpty()) {
			for (PropriedadesUsuarioExterno pue : propriedadesUsuarioExterno) {
				retorno.addAll(pue.getUfs());
			}
			return new ArrayList<UF>(retorno);
		}

		return Collections.emptyList();
	}

	public void alterouUf() {
		this.unidade = null;
	}

}
