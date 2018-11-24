package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ExcecaoResponsabilidades;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.UsuarioPerfil;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoAcao;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.TipoUsuario;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.ExcecaoResponsabilidadesService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioPerfilService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarExcecaoDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarExcecaoDesenvolvimentoBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private ExcecaoResponsabilidadesService excecaoResponsabilidadesService;

	@Inject
	private UsuarioPerfilService usuarioPerfilService;
	
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private ExcecaoResponsabilidades excecao = new ExcecaoResponsabilidades();

	private Boolean visualizando = Boolean.FALSE;

	private String nomeBusca;

	/** Usuario **/
	private boolean editandoUsuario = false;
	private List<Usuario> usuariosDisponiveis;
	private List<Usuario> usuariosSelecionadasVincular;
	private Usuario usuarioSelecionado;

	/** Unidades **/
	private boolean editandoUnidades = false;
	private List<Unidade> unidadesDisponiveis;
	private List<Unidade> unidadesSelecionadasVincular;
	private Unidade unidadeSelecionada;

	private List<Usuario> usuarioResponsaveis;

	private UF ufUnidades;

	private Usuario responsavelAtual;

	public void inserirParametro() {
		try {
			if (this.getExcecao().getUnidades() == null || this.getExcecao().getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("form-metas-resultados:tblUsuariosSelecionados",
						"Informe no m\u00EDnimo uma Unidade.");
				return;
			}

			if (this.getExcecao().getUsuarios() == null || this.getExcecao().getUsuarios().isEmpty()
					&& this.getExcecao().getTipoMeta() == TipoMeta.I) {
				FacesUtil.addErrorMessage("form-metas-resultados:tblUsuariosSelecionados",
						"Informe no m\u00EDnimo um Usu\u00E1rio.");
				return;
			}

			if (!getExcecao().getValidarResultadoMeta() && !getExcecao().getValidarTextoMeta()) {
				FacesUtil.addErrorMessage("form-metas-resultados:tipoAcaoResultados",
						"Selecione no m\u00EDnimo um Tipo de A\u00E7\u00E3o.");
			}

			if (this.cicloConfiguracao.getExcecoesResponsabilidades() == null) {
				this.cicloConfiguracao.setExcecoesResponsabilidades(new ArrayList<ExcecaoResponsabilidades>());
			}

			this.cicloConfiguracao.getExcecoesResponsabilidades().add(this.excecao);
			this.excecao.setCicloConfiguracao(this.cicloConfiguracao);
			verificaStatus();
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.excecao = new ExcecaoResponsabilidades();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void verificaStatus() {
		if (this.cicloConfiguracao.getExcecoesResponsabilidades() != null
				&& !this.cicloConfiguracao.getExcecoesResponsabilidades().isEmpty()) {
			this.cicloConfiguracao.setStatusConfiguracaoExcecaoResponsabilidades(StatusConfiguracao.C);
		} else {
			this.cicloConfiguracao.setStatusConfiguracaoExcecaoResponsabilidades(StatusConfiguracao.A);
		}
		
		try {
			cicloConfiguracaoService.save(cicloConfiguracao);
			cicloService.save(cicloConfiguracao.getCiclo());
		} catch (Exception e) {
			// do nothing
		}
	}

	public void excluir() {
		try {
			this.cicloConfiguracao.getExcecoesResponsabilidades().remove(this.excecao);
			verificaStatus();
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.excecao = new ExcecaoResponsabilidades();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametro() {
		try {
			if (this.getExcecao().getUnidades() == null || this.getExcecao().getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("form-metas-resultados:tblUsuariosSelecionados",
						"Informe no m\u00EDnimo uma Unidade.");
				return;
			}

			if (this.getExcecao().getUsuarios() == null || this.getExcecao().getUsuarios().isEmpty()) {
				FacesUtil.addErrorMessage("form-metas-resultados:tblUsuariosSelecionados",
						"Informe no m\u00EDnimo um Usu\u00E1rio.");
				return;
			}

			if (!getExcecao().getValidarResultadoMeta() && !getExcecao().getValidarTextoMeta()) {
				FacesUtil.addErrorMessage("form-metas-resultados:tipoAcaoResultados",
						"Selecione no m\u00EDnimo um Tipo de A\u00E7\u00E3o.");
			}

			this.cicloConfiguracaoService.save(cicloConfiguracao);
			this.excecao = new ExcecaoResponsabilidades();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editar(ExcecaoResponsabilidades cfg) {
		try {
			this.excecao = cfg;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * Usuarios
	 */
	public void prepararModalVincularUsuarios() {
		try {
			this.usuariosDisponiveis = usuarioService.findAllInternos();
			usuariosSelecionadasVincular = new ArrayList<Usuario>();
			this.nomeBusca = "";
			editandoUsuario = false;
			if (getExcecao().getUsuarios() != null) {
				usuariosDisponiveis.removeAll(getExcecao().getUsuarios());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarUsuariosVincular() {
		try {
			this.usuariosDisponiveis = usuarioService.findByNameLikeAndTipo(this.nomeBusca, TipoUsuario.I);
			if (getExcecao().getUsuarios() != null) {
				usuariosDisponiveis.removeAll(getExcecao().getUsuarios());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formUsuarioMonitoramento",
					"Erro ao buscar Usuarios, tente novamente mais tarde.");
		}
	}

	public void vincularUsuarios() {
		try {
			if (editandoUsuario) {
				this.getExcecao().setUsuarios(this.usuariosSelecionadasVincular);
			} else {
				if (this.getExcecao().getUsuarios() == null) {
					this.getExcecao().setUsuarios(new ArrayList<Usuario>());
				}

				List<Usuario> usuariosVinculadosTemp = this.getExcecao().getUsuarios();
				usuariosVinculadosTemp.addAll(this.usuariosSelecionadasVincular);

				Collections.sort(usuariosVinculadosTemp, new Comparator<Usuario>() {
					@Override
					public int compare(Usuario o1, Usuario o2) {
						return o1.getNome().compareTo(o2.getNome());
					}
				});
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void desvincularUsuario() {
		try {
			this.getExcecao().getUsuarios().remove(this.usuarioSelecionado);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUsuarioEditar() {
		try {
			usuariosDisponiveis = usuarioService.findAllInternos();
			usuariosSelecionadasVincular = getExcecao().getUsuarios();
			this.nomeBusca = "";
			editandoUsuario = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * Unidades
	 */
	public void prepararModalVincularUnidades() {
		try {
			this.ufUnidades = null;
			unidadesDisponiveis = unidadeService.findAll();
			unidadesSelecionadasVincular = new ArrayList<Unidade>();
			this.nomeBusca = "";
			editandoUnidades = false;
			if (getExcecao().getUnidades() != null) {
				unidadesDisponiveis.removeAll(getExcecao().getUnidades());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarUnidadesVincular() {
		try {
			this.unidadesDisponiveis = unidadeService.findByDescricao(this.nomeBusca);
			if (getExcecao().getUnidades() != null) {
				unidadesDisponiveis.removeAll(getExcecao().getUnidades());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void vincularUnidades() {
		try {
			if (editandoUnidades) {
				this.getExcecao().setUnidades(unidadesSelecionadasVincular);
			} else {
				if (this.getExcecao().getUnidades() == null) {
					this.getExcecao().setUnidades(new ArrayList<Unidade>());
				}
				List<Unidade> unidadesVinculadosTemp = this.getExcecao().getUnidades();
				unidadesVinculadosTemp.addAll(this.unidadesSelecionadasVincular);

				Collections.sort(unidadesVinculadosTemp, new Comparator<Unidade>() {
					@Override
					public int compare(Unidade o1, Unidade o2) {
						return o1.getDescricao().compareTo(o2.getDescricao());
					}
				});
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void desvincularUnidade() {
		try {
			this.getExcecao().getUnidades().remove(this.unidadeSelecionada);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			this.ufUnidades = null;
			unidadesDisponiveis = unidadeService.findAll();
			unidadesSelecionadasVincular = this.excecaoResponsabilidadesService
					.getUnidadesExcecaoResponsabilidades(getExcecao().getId());
			this.nomeBusca = "";
			editandoUnidades = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

	public boolean isEditandoUsuario() {
		return editandoUsuario;
	}

	public void setEditandoUsuario(boolean editandoUsuario) {
		this.editandoUsuario = editandoUsuario;
	}

	public List<Usuario> getUsuariosDisponiveis() {
		return usuariosDisponiveis;
	}

	public void setUsuariosDisponiveis(List<Usuario> usuariosDisponiveis) {
		this.usuariosDisponiveis = usuariosDisponiveis;
	}

	public List<Usuario> getUsuariosSelecionadasVincular() {
		return usuariosSelecionadasVincular;
	}

	public void setUsuariosSelecionadasVincular(List<Usuario> usuariosSelecionadasVincular) {
		this.usuariosSelecionadasVincular = usuariosSelecionadasVincular;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public Boolean getVisualizando() {
		return visualizando;
	}

	public void setVisualizando(Boolean visualizando) {
		this.visualizando = visualizando;
	}

	public List<TipoMeta> getTiposMeta() {
		return Arrays.asList(TipoMeta.values());
	}

	public List<TipoAcao> getTiposAcao() {
		return Arrays.asList(new TipoAcao[] { TipoAcao.VTM, TipoAcao.VRM });
	}

	public ExcecaoResponsabilidades getExcecao() {
		return excecao;
	}

	public void setExcecao(ExcecaoResponsabilidades excecao) {
		this.excecao = excecao;
	}

	public List<Usuario> getUsuariosResponsaveis() {
		if (this.usuarioResponsaveis == null || this.usuarioResponsaveis.isEmpty()) {
			this.usuarioResponsaveis = this.usuarioService.findAllInternos();
		}
		return this.usuarioResponsaveis;
	}

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public boolean isEditandoUnidades() {
		return editandoUnidades;
	}

	public void setEditandoUnidades(boolean editandoUnidades) {
		this.editandoUnidades = editandoUnidades;
	}

	public List<Unidade> getUnidadesDisponiveis() {
		return unidadesDisponiveis;
	}

	public void alterouUfFiltroUnidade() {
		if (this.ufUnidades != null) {
			this.unidadesDisponiveis = unidadeService.findByUf(this.ufUnidades);
		} else {
			this.unidadesDisponiveis = unidadeService.findAll();
		}
	}

	public void setUnidadesDisponiveis(List<Unidade> unidadesDisponiveis) {
		this.unidadesDisponiveis = unidadesDisponiveis;
	}

	public List<Unidade> getUnidadesSelecionadasVincular() {
		return unidadesSelecionadasVincular;
	}

	public void setUnidadesSelecionadasVincular(List<Unidade> unidadesSelecionadasVincular) {
		this.unidadesSelecionadasVincular = unidadesSelecionadasVincular;
	}

	public UF getUfUnidades() {
		return ufUnidades;
	}

	public void setUfUnidades(UF ufUnidades) {
		this.ufUnidades = ufUnidades;
	}

	public Usuario getResponsavelAtual() {
		if (this.excecao.getUnidades() != null && !this.excecao.getUnidades().isEmpty()) {
			List<UsuarioPerfil> usuariosPerfil = usuarioPerfilService.findByTipoPerfilAndUnidades(Perfil.GERENTE,
					this.excecao.getUnidades());
			if (usuariosPerfil != null && !usuariosPerfil.isEmpty()) {
				this.responsavelAtual = usuariosPerfil.get(0).getUsuario();
			}
		}
		return responsavelAtual;
	}

	public void setResponsavelAtual(Usuario responsavelAtual) {
		this.responsavelAtual = responsavelAtual;
	}

}
