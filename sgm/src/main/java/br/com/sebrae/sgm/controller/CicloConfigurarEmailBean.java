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
import br.com.sebrae.sgm.model.ConfiguracaoEmail;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.PeriodicidadeEnvio;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.ConfiguracaoEmailService;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarEmailBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarEmailBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;
	@Inject
	private PerfilService perfilService;
	@Inject
	private UnidadeService unidadeService;
	@Inject
	private UsuarioService usuarioService;
	@Inject
	private ConfiguracaoEmailService configuracaoEmailService;

	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;
	private ConfiguracaoEmail configuracaoEmail = new ConfiguracaoEmail();
	private Boolean visualizando = Boolean.FALSE;

	private List<Perfil> perfis;

	/** Unidades **/
	private String nomeBusca = "";

	private boolean editandoUnidades = false;
	private List<Unidade> unidadesDisponiveis;
	private List<Unidade> unidadesSelecionadasVincular;
	private Unidade unidadeSelecionada;

	/** Usuario **/
	private boolean editandoUsuario = false;
	private List<Usuario> usuariosDisponiveis;
	private List<Usuario> usuariosSelecionadasVincular;
	private Usuario usuarioSelecionado;

	public String mensagemSucesso() {
		FacesUtil.addInfoMessage("Par\u00E2metros salvos com sucesso!");
		return "/pages/ciclo/manter.xhtml";
	}

	public void inserirParametro() {
		try {
			if (this.configuracaoEmail.getUnidades() == null
					|| this.configuracaoEmail.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage(
						"form-configuracao-email:tblUnidadesSelecionadas",
						"Selecione no m\u00EDnimo uma Unidade.");
				return;
			}

			if ((this.configuracaoEmail.getUsuarios() == null || this.configuracaoEmail
					.getUsuarios().isEmpty())
					&& (this.configuracaoEmail.getUsuarios() == null || this.configuracaoEmail
							.getUsuarios().isEmpty())) {
				FacesUtil.addErrorMessage(
						"form-configuracao-email:tblUsuariosAdicionados",
						"Selecione no m\u00EDnimo um Usu\u00E1rio");
				return;
			}

			if (this.cicloConfiguracao.getConfiguracoesEmails() == null) {
				this.cicloConfiguracao
						.setConfiguracoesEmails(new ArrayList<ConfiguracaoEmail>());
			}

			this.cicloConfiguracao.getConfiguracoesEmails().add(
					this.configuracaoEmail);
			this.configuracaoEmail.setCicloConfiguracao(this.cicloConfiguracao);
			verificaStatus();
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.configuracaoEmail = new ConfiguracaoEmail();
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void verificaStatus() {
		if (this.cicloConfiguracao.getConfiguracoesEmails() != null
				&& !this.cicloConfiguracao.getConfiguracoesEmails().isEmpty()) {
			this.cicloConfiguracao
					.setStatusConfiguracaoEmail(StatusConfiguracao.C);
		} else {
			this.cicloConfiguracao
					.setStatusConfiguracaoEmail(StatusConfiguracao.A);
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
			this.cicloConfiguracao.getConfiguracoesEmails().remove(
					getConfiguracaoEmail());
			verificaStatus();
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.configuracaoEmail = new ConfiguracaoEmail();
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametro() {
		try {
			if (this.configuracaoEmail.getUnidades() == null
					|| this.configuracaoEmail.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage(
						"form-configuracao-email:tblUnidadesSelecionadas",
						"Selecione no m\u00EDnimo uma Unidade.");
				return;
			}

			if ((this.configuracaoEmail.getUsuarios() == null || this.configuracaoEmail
					.getUsuarios().isEmpty())
					&& (this.configuracaoEmail.getUsuarios() == null || this.configuracaoEmail
							.getUsuarios().isEmpty())) {
				FacesUtil.addErrorMessage(
						"form-configuracao-email:tblUsuariosAdicionados",
						"Selecione no m\u00EDnimo um Usu\u00E1rio");
				return;
			}

			this.cicloConfiguracao.getConfiguracoesEmails().remove(
					this.configuracaoEmail);
			this.cicloConfiguracao.getConfiguracoesEmails().add(
					this.configuracaoEmail);
			this.cicloConfiguracaoService.save(cicloConfiguracao);
			this.configuracaoEmail = new ConfiguracaoEmail();
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editar(ConfiguracaoEmail cfg) {
		try {
			this.configuracaoEmail = cfg.clone();
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * Unidades
	 */
	public void prepararModalVincularUnidades() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this
					.getCicloConfiguracao().getCiclo().getUf());
			unidadesSelecionadasVincular = new ArrayList<Unidade>();
			this.nomeBusca = "";
			editandoUnidades = false;
			if (getConfiguracaoEmail().getUnidades() != null) {
				unidadesDisponiveis.removeAll(getConfiguracaoEmail()
						.getUnidades());
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void vincularUnidades() {
		try {
			if (editandoUnidades) {
				this.getConfiguracaoEmail().setUnidades(
						unidadesSelecionadasVincular);
			} else {
				if (this.getConfiguracaoEmail().getUnidades() == null) {
					this.getConfiguracaoEmail().setUnidades(
							new ArrayList<Unidade>());
				}
				List<Unidade> unidadesVinculadosTemp = this
						.getConfiguracaoEmail().getUnidades();
				unidadesVinculadosTemp
						.addAll(this.unidadesSelecionadasVincular);

				Collections.sort(unidadesVinculadosTemp,
						new Comparator<Unidade>() {
							@Override
							public int compare(Unidade o1, Unidade o2) {
								return o1.getDescricao().compareTo(
										o2.getDescricao());
							}
						});
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void desvincularUnidade() {
		try {
			this.getConfiguracaoEmail().getUnidades()
					.remove(this.unidadeSelecionada);
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			unidadesDisponiveis = unidadeService
					.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = this.configuracaoEmailService
					.getUnidadesConfiguracaoEmail(getConfiguracaoEmail()
							.getId());
			this.nomeBusca = "";
			editandoUnidades = true;
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * Usuarios
	 */
	public void prepararModalVincularUsuarios() {
		try {
			if (this.configuracaoEmail.getPerfis() != null
					&& !this.configuracaoEmail.getPerfis().isEmpty()
					&& this.configuracaoEmail.getUnidades() != null
					&& !this.configuracaoEmail.getUnidades().isEmpty()) {
				this.usuariosDisponiveis = new ArrayList<Usuario>();
				if (this.configuracaoEmail.getPerfis() != null
						&& !this.configuracaoEmail.getPerfis().isEmpty()
						&& this.configuracaoEmail.getUnidades() != null
						&& !this.configuracaoEmail.getUnidades().isEmpty()) {
					this.usuariosDisponiveis.addAll(usuarioService
							.findByNameLikeAndPerfisAndUnidadesInternos(
									this.nomeBusca,
									this.configuracaoEmail.getPerfis(),
									this.configuracaoEmail.getUnidades()));
					this.usuariosDisponiveis.addAll(usuarioService
							.findByNameLikeAndPerfisAndUnidadesExternos(
									this.nomeBusca,
									this.configuracaoEmail.getPerfis(),
									this.configuracaoEmail.getUnidades()));
				}
				usuariosSelecionadasVincular = new ArrayList<Usuario>();
				this.nomeBusca = "";
				editandoUsuario = false;
				if (getConfiguracaoEmail().getUsuarios() != null) {
					usuariosDisponiveis.removeAll(getConfiguracaoEmail()
							.getUsuarios());
				}
			} else {
				this.usuariosDisponiveis = new ArrayList<Usuario>();
				if (this.configuracaoEmail.getPerfis() == null
						|| this.configuracaoEmail.getPerfis().isEmpty()) {
					FacesUtil.addErrorMessage("formUsuario",
							"Informe o perfil para busca de usu\u00E1rios.");
				} else {
					if (this.configuracaoEmail.getUnidades() == null
							|| this.configuracaoEmail.getUnidades().isEmpty()) {
						FacesUtil
								.addErrorMessage("formUsuario",
										"Informe a unidade para busca de usu\u00E1rios.");
					}
				}
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarUsuariosVincular() {
		try {
			this.usuariosDisponiveis = new ArrayList<Usuario>();
			this.usuariosDisponiveis.addAll(usuarioService
					.findByNameLikeAndPerfisAndUnidadesInternos(this.nomeBusca,
							this.configuracaoEmail.getPerfis(),
							this.configuracaoEmail.getUnidades()));
			this.usuariosDisponiveis.addAll(usuarioService
					.findByNameLikeAndPerfisAndUnidadesExternos(this.nomeBusca,
							this.configuracaoEmail.getPerfis(),
							this.configuracaoEmail.getUnidades()));

			if (getConfiguracaoEmail().getUsuarios() != null) {
				usuariosDisponiveis.removeAll(getConfiguracaoEmail()
						.getUsuarios());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formUsuario",
					"Erro ao buscar Usuarios, tente novamente mais tarde.");
		}
	}

	public void vincularUsuarios() {
		try {
			if (editandoUsuario) {
				this.getConfiguracaoEmail().setUsuarios(
						this.usuariosSelecionadasVincular);
			} else {
				if (this.getConfiguracaoEmail().getUsuarios() == null) {
					this.getConfiguracaoEmail().setUsuarios(
							new ArrayList<Usuario>());
				}

				List<Usuario> usuariosVinculadosTemp = this
						.getConfiguracaoEmail().getUsuarios();
				usuariosVinculadosTemp
						.addAll(this.usuariosSelecionadasVincular);

				Collections.sort(usuariosVinculadosTemp,
						new Comparator<Usuario>() {
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
			this.getConfiguracaoEmail().getUsuarios()
					.remove(this.usuarioSelecionado);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil
					.addErrorMessage("Erro ao desvincular Usuario, tente novamente mais tarde.");
		}
	}

	public void prepararModalVincularUsuarioEditar() {
		try {
			if (this.getConfiguracaoEmail().getPerfis() != null
					&& !this.configuracaoEmail.getPerfis().isEmpty()) {
				this.usuariosDisponiveis = new ArrayList<Usuario>();
				if (this.configuracaoEmail.getPerfis() != null
						&& !this.configuracaoEmail.getPerfis().isEmpty()
						&& this.configuracaoEmail.getUnidades() != null
						&& !this.configuracaoEmail.getUnidades().isEmpty()) {
					this.usuariosDisponiveis.addAll(usuarioService
							.findByNameLikeAndPerfisAndUnidadesInternos(
									this.nomeBusca,
									this.configuracaoEmail.getPerfis(),
									this.configuracaoEmail.getUnidades()));
					this.usuariosDisponiveis.addAll(usuarioService
							.findByNameLikeAndPerfisAndUnidadesExternos(
									this.nomeBusca,
									this.configuracaoEmail.getPerfis(),
									this.configuracaoEmail.getUnidades()));
				}
				usuariosSelecionadasVincular = configuracaoEmailService
						.getUsuarioConfiguracaoEmail(getConfiguracaoEmail()
								.getId());
				this.nomeBusca = "";
				editandoUsuario = true;
			} else {
				FacesUtil
						.addErrorMessage("formUsuario",
								"\u00C9 necess\u00E1rio informar um perfil para listagem dos usu\u00E1rios.");
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterouPerfil() {
		if (this.configuracaoEmail.getUsuarios() != null) {
			this.configuracaoEmail.getUsuarios().clear();
		}
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

	public List<Perfil> getPerfis() {
		if (perfis == null || perfis.isEmpty()) {
			perfis = perfilService.bindByChaves(Arrays
					.asList(Perfil.PERFIS_CHEFIA_USUARIO_EXTERNO));
		}
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	public List<Fase> getFasesCiclo() {
		return Arrays.asList(new Fase[] { Fase.P, Fase.R, Fase.J, Fase.V });
	}

	public List<Fase> getFasesDesenvolvimentoCiclo() {
		return Arrays.asList(new Fase[] { Fase.P, Fase.R, Fase.J, Fase.V });
	}

	public List<PeriodicidadeEnvio> getPeriodicidades() {
		return Arrays.asList(PeriodicidadeEnvio.values());
	}

	public ConfiguracaoEmail getConfiguracaoEmail() {
		return configuracaoEmail;
	}

	public void setConfiguracaoEmail(ConfiguracaoEmail configuracaoEmail) {
		this.configuracaoEmail = configuracaoEmail;
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
		return unidadesDisponiveis;
	}

	public void setUnidadesDisponiveis(List<Unidade> unidadesDisponiveis) {
		this.unidadesDisponiveis = unidadesDisponiveis;
	}

	public List<Unidade> getUnidadesSelecionadasVincular() {
		return unidadesSelecionadasVincular;
	}

	public void setUnidadesSelecionadasVincular(
			List<Unidade> unidadesSelecionadasVincular) {
		this.unidadesSelecionadasVincular = unidadesSelecionadasVincular;
	}

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
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

	public void setUsuariosSelecionadasVincular(
			List<Usuario> usuariosSelecionadasVincular) {
		this.usuariosSelecionadasVincular = usuariosSelecionadasVincular;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

}
