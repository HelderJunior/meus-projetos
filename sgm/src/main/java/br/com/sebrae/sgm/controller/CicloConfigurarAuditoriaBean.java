package br.com.sebrae.sgm.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Anexo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoAuditoria;
import br.com.sebrae.sgm.model.ParametrosAuditoria;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoParametroAuditoria;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarAuditoriaBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarAuditoriaBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private PerfilService perfilService;

	@Inject
	private UsuarioService usuarioService;
	
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private Boolean visualizando = Boolean.FALSE;

	private ConfiguracaoAuditoria configuracaoAuditoria;

	private int indiceAnexo;

	// Metas de Equipes
	private ParametrosAuditoria parametroAuditoriaEquipe = new ParametrosAuditoria();

	private ParametrosAuditoria parametroAuditoriaColaborador = new ParametrosAuditoria();

	/** Unidades **/
	private String nomeBusca;

	private boolean editandoUnidades = false;
	private List<Unidade> unidadesDisponiveis;
	private List<Unidade> unidadesSelecionadasVincular;
	private Unidade unidadeSelecionada;

	private List<Perfil> perfis;

	/** Usuario **/
	private Unidade unidadeUsuario;
	private boolean editandoUsuario = false;
	private List<Usuario> usuariosDisponiveis;
	private List<Usuario> usuariosSelecionadasVincular;
	private Usuario usuarioSelecionado;

	/*
	 * Unidades
	 */
	public void prepararModalVincularUnidades() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = new ArrayList<Unidade>();
			this.nomeBusca = "";
			editandoUnidades = false;
			if (getParametroAuditoriaEquipe().getUnidades() != null) {
				unidadesDisponiveis.removeAll(getParametroAuditoriaEquipe().getUnidades());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void vincularUnidades() {
		try {
			if (editandoUnidades) {
				this.getParametroAuditoriaEquipe().setUnidades(unidadesSelecionadasVincular);
			} else {
				if (this.getParametroAuditoriaEquipe().getUnidades() == null) {
					this.getParametroAuditoriaEquipe().setUnidades(new ArrayList<Unidade>());
				}
				List<Unidade> unidadesVinculadosTemp = this.getParametroAuditoriaEquipe().getUnidades();
				unidadesVinculadosTemp.addAll(this.unidadesSelecionadasVincular);

				Collections.sort(unidadesVinculadosTemp, new Comparator<Unidade>() {
					@Override
					public int compare(Unidade o1, Unidade o2) {
						return o1.getDescricao().compareTo(o2.getDescricao());
					}
				});
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void desvincularUnidade() {
		try {
			this.getParametroAuditoriaEquipe().getUnidades().remove(this.unidadeSelecionada);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao desvincular Unidade, tente novamente mais tarde.");
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = getParametroAuditoriaEquipe().getUnidades();
			this.nomeBusca = "";
			editandoUnidades = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao editar Unidade, tente novamente mais tarde.");
		}
	}

	public void inserirParametroEquipe() {
		try {
			if (formularioAuditoriaValido()) {
				if (this.getParametroAuditoriaEquipe().getUnidades() == null
						|| this.getParametroAuditoriaEquipe().getUnidades().isEmpty()) {
					FacesUtil.addErrorMessage("form-metas-equipes:tblUnidadesSelecionadas",
							"Informe no minimo uma Unidade.");
					return;
				}

				if (this.cicloConfiguracao.getConfiguracaoAuditoria() == null) {
					this.cicloConfiguracao.setConfiguracaoAuditoria(this.configuracaoAuditoria);
				}

				if (this.configuracaoAuditoria.getParametrosAuditoria() == null) {
					this.configuracaoAuditoria.setParametrosAuditoria(new ArrayList<ParametrosAuditoria>());
				}

				this.cicloConfiguracao.getConfiguracaoAuditoria().getParametrosAuditoria()
						.add(this.parametroAuditoriaEquipe);
				this.parametroAuditoriaEquipe.setConfiguracaoAuditoria(this.configuracaoAuditoria);
				this.parametroAuditoriaEquipe.setTipoParametro(TipoParametroAuditoria.E);
				this.configuracaoAuditoria.setCicloConfiguracao(this.cicloConfiguracao);
				this.configuracaoAuditoria.setId(this.cicloConfiguracao.getId());
				verificaStatus();
				this.cicloConfiguracaoService.save(this.cicloConfiguracao);
				this.parametroAuditoriaEquipe = new ParametrosAuditoria();
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void verificaStatus() {
		if (this.cicloConfiguracao.getConfiguracaoAuditoria() != null
				&& this.cicloConfiguracao.getConfiguracaoAuditoria().getParametrosAuditoria() != null
				&& !this.cicloConfiguracao.getConfiguracaoAuditoria().getParametrosAuditoria().isEmpty()) {
			this.cicloConfiguracao.setStatusConfiguracaoAuditoria(StatusConfiguracao.C);
		} else {
			this.cicloConfiguracao.setStatusConfiguracaoAuditoria(StatusConfiguracao.A);
		}
		
		try {
			cicloConfiguracaoService.save(cicloConfiguracao);
			cicloService.save(cicloConfiguracao.getCiclo());
		} catch (Exception e) {
			// do nothing
		}
	}

	public void excluirParametroEquipe() {
		try {
			this.cicloConfiguracao.getConfiguracaoAuditoria().getParametrosAuditoria()
					.remove(getParametroAuditoriaEquipe());
			verificaStatus();
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.parametroAuditoriaEquipe = new ParametrosAuditoria();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametroEquipe() {
		try {
			if (formularioAuditoriaValido()) {
				if (this.getParametroAuditoriaEquipe().getUnidades() == null
						|| this.getParametroAuditoriaEquipe().getUnidades().isEmpty()) {
					FacesUtil.addErrorMessage("form-metas-equipes:tblUnidadesSelecionadas",
							"Informe no minimo uma Unidade.");
					return;
				}
				this.cicloConfiguracaoService.save(cicloConfiguracao);
				this.parametroAuditoriaEquipe = new ParametrosAuditoria();
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editarEquipe(ParametrosAuditoria obj) {
		try {
			this.parametroAuditoriaEquipe = obj;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterouUfPerfilUsuarioEquipe() {
		this.parametroAuditoriaEquipe.setUsuario(null);
	}

	/*
	 * Usuarios
	 */
	public void prepararModalVincularUsuarios() {
		try {
			usuariosDisponiveis = usuarioService.findByUF(this.cicloConfiguracao.getCiclo().getUf());
			usuariosSelecionadasVincular = new ArrayList<Usuario>();
			this.nomeBusca = "";
			editandoUsuario = false;
			if (getParametroAuditoriaColaborador().getUsuarios() != null) {
				usuariosDisponiveis.removeAll(getParametroAuditoriaColaborador().getUsuarios());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarUsuariosVincular() {
		try {
			this.usuariosDisponiveis = usuarioService.findByNomeUF(this.nomeBusca, this.cicloConfiguracao.getCiclo()
					.getUf());
			if (getParametroAuditoriaColaborador().getUsuarios() != null) {
				usuariosDisponiveis.removeAll(getParametroAuditoriaColaborador().getUsuarios());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formUsuario", "Erro ao buscar Usuarios, tente novamente mais tarde.");
		}
	}

	public void vincularUsuarios() {
		try {
			if (editandoUsuario) {
				this.getParametroAuditoriaColaborador().setUsuarios(this.usuariosSelecionadasVincular);
			} else {
				if (this.getParametroAuditoriaColaborador().getUsuarios() == null) {
					this.getParametroAuditoriaColaborador().setUsuarios(new ArrayList<Usuario>());
				}

				List<Usuario> usuariosVinculadosTemp = this.getParametroAuditoriaColaborador().getUsuarios();
				usuariosVinculadosTemp.addAll(this.usuariosSelecionadasVincular);

				Collections.sort(usuariosVinculadosTemp, new Comparator<Usuario>() {
					@Override
					public int compare(Usuario o1, Usuario o2) {
						return o1.getNome().compareTo(o2.getNome());
					}
				});
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void desvincularUsuario() {
		try {
			this.getParametroAuditoriaColaborador().getUsuarios().remove(this.usuarioSelecionado);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao desvincular Usuario, tente novamente mais tarde.");
		}
	}

	public void prepararModalVincularUsuarioEditar() {
		try {
			usuariosDisponiveis = usuarioService.findByUF(this.cicloConfiguracao.getCiclo().getUf());
			usuariosSelecionadasVincular = getParametroAuditoriaColaborador().getUsuarios();
			this.nomeBusca = "";
			editandoUsuario = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Colaborador
	 */
	public void inserirParametroColaborador() {
		try {

			if (formularioAuditoriaValido()) {
				if (this.getParametroAuditoriaColaborador().getUsuarios() == null
						|| this.getParametroAuditoriaColaborador().getUsuarios().isEmpty()) {
					FacesUtil.addErrorMessage("form-colaborador:tblUsuariosAdicionados",
							"Informe no m\u00EDnimo um Usu\u00E1rio.");
					return;
				}

				if (this.cicloConfiguracao.getConfiguracaoAuditoria() == null) {
					this.cicloConfiguracao.setConfiguracaoAuditoria(this.configuracaoAuditoria);
				}

				if (this.configuracaoAuditoria.getParametrosAuditoria() == null) {
					this.configuracaoAuditoria.setParametrosAuditoria(new ArrayList<ParametrosAuditoria>());
				}

				this.cicloConfiguracao.getConfiguracaoAuditoria().getParametrosAuditoria()
						.add(this.parametroAuditoriaColaborador);
				this.parametroAuditoriaColaborador.setConfiguracaoAuditoria(this.configuracaoAuditoria);
				this.parametroAuditoriaColaborador.setTipoParametro(TipoParametroAuditoria.C);
				this.configuracaoAuditoria.setCicloConfiguracao(this.cicloConfiguracao);
				this.configuracaoAuditoria.setId(this.cicloConfiguracao.getId());
				verificaStatus();
				this.cicloConfiguracaoService.save(this.cicloConfiguracao);
				this.parametroAuditoriaColaborador = new ParametrosAuditoria();
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirParametroColaborador() {
		try {
			this.cicloConfiguracao.getConfiguracaoAuditoria().getParametrosAuditoria()
					.remove(getParametroAuditoriaColaborador());
			verificaStatus();
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.parametroAuditoriaEquipe = new ParametrosAuditoria();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametroColaborador() {
		try {
			if (formularioAuditoriaValido()) {
				if (this.getParametroAuditoriaColaborador().getUsuarios() == null
						|| this.getParametroAuditoriaColaborador().getUsuarios().isEmpty()) {
					FacesUtil.addErrorMessage("form-colaborador:tblUsuariosAdicionados",
							"Informe no m\u00EDnimo um Usu\u00E1rio.");
					return;
				}
				this.cicloConfiguracaoService.save(cicloConfiguracao);
				this.parametroAuditoriaColaborador = new ParametrosAuditoria();
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editarColaborador(ParametrosAuditoria obj) {
		try {
			this.parametroAuditoriaColaborador = obj;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterouUnidadeFiltroColaborador() {
		if (this.unidadeUsuario != null) {
			usuariosDisponiveis = usuarioService.findByUnidade(this.unidadeUsuario);
		} else {
			usuarioService.findByUF(this.cicloConfiguracao.getCiclo().getUf());
		}
		usuariosSelecionadasVincular = new ArrayList<Usuario>();
	}

	public void alterouUfPerfilUsuarioColaborador() {
		this.parametroAuditoriaColaborador.setUsuario(null);
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

	public ConfiguracaoAuditoria getConfiguracaoAuditoria() {

		if (this.cicloConfiguracao.getConfiguracaoAuditoria() != null) {
			this.configuracaoAuditoria = this.cicloConfiguracao.getConfiguracaoAuditoria();
		}

		if (this.configuracaoAuditoria == null) {
			this.configuracaoAuditoria = new ConfiguracaoAuditoria();
		}

		return configuracaoAuditoria;
	}

	public void setConfiguracaoAuditoria(ConfiguracaoAuditoria configuracaoAuditoria) {
		this.configuracaoAuditoria = configuracaoAuditoria;
	}

	public void uploadArquivo(FileUploadEvent event) {
		try {
			String fileName = FilenameUtils.getName(event.getFile().getFileName());

			if (this.configuracaoAuditoria.getAnexos() == null) {
				this.configuracaoAuditoria.setAnexos(new ArrayList<Anexo>());
			}

			Anexo an = new Anexo();
			an.setNome(RandomStringUtils.random(8, true, true) + fileName);
			an.setNomeExibicao(fileName);

			byte[] fileBytes = IOUtils.toByteArray(event.getFile().getInputstream());

			an.setTipo(event.getFile().getContentType());
			an.setArquivo(fileBytes);

			FileUtils
					.writeByteArrayToFile(new File(Constants.FILES_TMP_DIR + File.separator + an.getNome()), fileBytes);

			an.setConfiguracaoAuditoria(this.configuracaoAuditoria);
			this.configuracaoAuditoria.getAnexos().add(an);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirArquivo() {
		this.getConfiguracaoAuditoria().getAnexos().remove(this.indiceAnexo);
	}

	public StreamedContent getArquivo() {
		StreamedContent file = null;
		try {
			Anexo a = this.getConfiguracaoAuditoria().getAnexos().get(indiceAnexo);
			if (a.getArquivo() != null) {
				ByteArrayInputStream fis;
				fis = new ByteArrayInputStream(a.getArquivo());
				file = new DefaultStreamedContent(fis, a.getTipo(), a.getNomeExibicao());
			} else {
				FacesUtil.addErrorMessage("Nenhum arquivo a ser exibido.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return file;
	}

	public int getIndiceAnexo() {
		return indiceAnexo;
	}

	public void setIndiceAnexo(int indiceAnexo) {
		this.indiceAnexo = indiceAnexo;
	}

	public ParametrosAuditoria getParametroAuditoriaEquipe() {
		return parametroAuditoriaEquipe;
	}

	public void setParametroAuditoriaEquipe(ParametrosAuditoria parametroAuditoriaEquipe) {
		this.parametroAuditoriaEquipe = parametroAuditoriaEquipe;
	}

	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

	public boolean isEditandoUnidades() {
		return editandoUnidades;
	}

	public void setEditandoUnidades(boolean editandoUnidades) {
		this.editandoUnidades = editandoUnidades;
	}

	public List<Unidade> getUnidadesDisponiveis() {
		this.unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
		return unidadesDisponiveis;
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

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public List<UF> getUfs() {
		return Arrays.asList(UF.values());
	}

	public List<Perfil> getPerfis() {
		if (perfis == null || perfis.isEmpty()) {
			perfis = perfilService.bindByChaves(Arrays.asList(new String[] { Perfil.COLABORADOR_CHAVE,
					Perfil.USUARIO_EXTERNO_CHAVE }));
		}
		return perfis;
	}

	public List<Usuario> getUsuarios() {
		List<Usuario> retorno = new ArrayList<Usuario>();
		if (this.parametroAuditoriaEquipe.getUfUsuario() != null && this.parametroAuditoriaEquipe.getPerfil() != null) {
			if (this.parametroAuditoriaEquipe.getPerfil().isExterno()) {
				retorno = usuarioService.findExternosUF(this
						.getCicloConfiguracao().getCiclo().getUf());
			} else {
				retorno = usuarioService.findInternosByPerfilUF(this.parametroAuditoriaEquipe.getPerfil(), this.parametroAuditoriaEquipe.getUfUsuario());
			}
		}
		return retorno;
	}

	public List<Usuario> getUsuariosColaborador() {
		List<Usuario> retorno = new ArrayList<Usuario>();
		if (this.parametroAuditoriaColaborador.getUfUsuario() != null && this.parametroAuditoriaColaborador.getPerfil() != null) {
			if (this.parametroAuditoriaColaborador.getPerfil().isExterno()) {
				retorno = usuarioService.findExternosUF(this
						.getCicloConfiguracao().getCiclo().getUf());
			} else {
				retorno = usuarioService.findInternosByPerfilUF(this.parametroAuditoriaColaborador.getPerfil(), this.parametroAuditoriaColaborador.getUfUsuario());
			}
		}
		return retorno;
	}

	public boolean formularioAuditoriaValido() {
		boolean retorno = true;

		if (getConfiguracaoAuditoria().getDtInicio() == null) {
			FacesUtil
					.addErrorMessage("form-auditoria:dtInicioAuditoria", "Data de In\u00EDcio: campo obrigat\u00F3rio");
			retorno = false;
		}

		if (getConfiguracaoAuditoria().getDtFim() == null) {
			FacesUtil.addErrorMessage("form-auditoria:dtFimAuditoria", "Data Fim: campo obrigat\u00F3rio");
			retorno = false;
		}

		if (getConfiguracaoAuditoria().getDtInicio() != null && getConfiguracaoAuditoria().getDtFim() != null) {
			if (getConfiguracaoAuditoria().getDtInicio().compareTo(getConfiguracaoAuditoria().getDtFim()) > 0) {
				FacesUtil.addErrorMessage("form-auditoria:dtFimAuditoria",
						"Data Fim n\u00E3o pode ser menor que a Data de In\u00EDcio");
				retorno = false;
			}
		}

		// if (org.apache.commons.lang3.StringUtils.isBlank(getConfiguracaoAuditoria().getObservacao())) {
		// FacesUtil.addErrorMessage("form-auditoria:obsAuditoria", "Observa\u00E7\u00F5es: campo obrigat\u00F3rio.");
		// retorno = false;
		// }

		/*
		 * if (getConfiguracaoAuditoria().getAnexos() == null || getConfiguracaoAuditoria().getAnexos().isEmpty()) {
		 * FacesUtil.addErrorMessage("form-auditoria:tblAnexos", "Informe no m\u00EDnimo um anexo."); retorno = false; }
		 */

		return retorno;
	}

	public ParametrosAuditoria getParametroAuditoriaColaborador() {
		return parametroAuditoriaColaborador;
	}

	public void setParametroAuditoriaColaborador(ParametrosAuditoria parametroAuditoriaColaborador) {
		this.parametroAuditoriaColaborador = parametroAuditoriaColaborador;
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

	public String continuarConfiguracao() {
		verificaStatus();
		FacesUtil.addInfoMessage("Par\u00E2metros salvos com sucesso!");
		return "/pages/ciclo/manter.xhtml";
	}

	public Unidade getUnidadeUsuario() {
		return unidadeUsuario;
	}

	public void setUnidadeUsuario(Unidade unidadeUsuario) {
		this.unidadeUsuario = unidadeUsuario;
	}

	public List<Unidade> completeUnidade(String query) {
		List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUF(query, this.cicloConfiguracao.getCiclo()
				.getUf());
		return retorno;
	}

}
