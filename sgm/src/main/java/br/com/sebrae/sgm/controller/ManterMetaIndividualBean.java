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

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoMarcoCritico;
import br.com.sebrae.sgm.model.Entrega;
import br.com.sebrae.sgm.model.FluxoAprovacaoMetas;
import br.com.sebrae.sgm.model.Indicador;
import br.com.sebrae.sgm.model.MarcoCritico;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.MetaStatus;
import br.com.sebrae.sgm.model.Observacao;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Projeto;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.audit.listener.TipoAlteracaoTheadLocal;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.FormaCalculo;
import br.com.sebrae.sgm.model.enums.Mes;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.StatusEnvio;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoCampoObservacao;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoDado;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoLog;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.TipoObservacao;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.ConfiguracaoMarcoCriticoService;
import br.com.sebrae.sgm.service.IndicadorService;
import br.com.sebrae.sgm.service.LogService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.ProjetoService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.utils.FacesUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@ConversationScoped
@Named
public class ManterMetaIndividualBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ManterMetaIndividualBean.class);

	@Inject
	private IndicadorService indicadorService;

	@Inject
	private ProjetoService projetoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private MetaService metaService;

	@Inject
	private AppBean appBean;

	@Inject
	private ListarMetasIndividuaisBean listarMetasBean;

	@Inject
	private PendenciasBean pendenciasBean;

	@Inject
	private LogService logService;

	@Inject
	private ConfiguracaoMarcoCriticoService configuracaoMarcoCriticoService;

	private Meta meta;

	private List<Indicador> indicadores = new ArrayList<Indicador>();

	private List<Projeto> projetosVincular = new ArrayList<Projeto>();
	private List<Projeto> projetosSelecionadosVincular = new ArrayList<Projeto>();
	private boolean editandoProjetos = false;
	private Projeto projetoSelecionado;

	private List<Unidade> unidadesVincular = new ArrayList<Unidade>();
	private List<Unidade> unidadesSelecionadasVincular = new ArrayList<Unidade>();
	private boolean editandoUnidades = false;
	private Unidade unidadeSelecionada;

	private MarcoCritico marcoCriticoSelecionado;

	private String nomeBusca;

	private List<Observacao> observacoesGerente = new ArrayList<Observacao>();
	private List<Observacao> observacoesComite = new ArrayList<Observacao>();
	private List<Observacao> observacoesUGP = new ArrayList<Observacao>();
	private List<Observacao> observacoesMonitoramento = new ArrayList<Observacao>();

	private Observacao observacaoSelecionada;

	private boolean visualizando = false;

	private List<Object[]> registrosLog;

	private TipoCampoObservacao tipoCampoObservacao;

	private UF ufUnidades;

	@Override
	public void init() {
		super.init();
		if (this.meta == null) {
			this.meta = new Meta();
			this.meta.setColaborador((Usuario) appBean.getUsuarioAutenticado());
			br.com.sebrae.sgm.model.MetaStatus ms = new MetaStatus();
			ms.setFase(Fase.P);
			ms.setStatus(StatusMeta.SA);
			ms.setMeta(this.meta);
			this.meta.adicionarMetaStatus(ms);
			this.meta.setCicloConfiguracao(this.appBean.getCicloConfiguracaoDesempenho());

			this.meta.setEntregas(new ArrayList<Entrega>());

			for (Mes m : Mes.values()) {
				meta.getEntregas().add(new Entrega(m, this.meta));
			}

			if (this.appBean.getUsuarioAutenticado().getUnidades() != null && !this.appBean.getUnidadesUf().isEmpty()) {
				this.meta.setUnidade(this.appBean.getUnidadesUf().get(0));
			}
		}
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public List<TipoDado> getTiposDado() {
		return Arrays.asList(TipoDado.values());
	}

	public List<FormaCalculo> getFormasCalculo() {
		return Arrays.asList(FormaCalculo.values());
	}

	public List<Mes> getMeses() {
		return Arrays.asList(Mes.values());
	}

	public List<Indicador> getIndicadores() {
		if (indicadores == null || indicadores.isEmpty()) {
			indicadores = indicadorService.findAllAtivosAndUF(appBean.getUsuarioAutenticado().getUnidade().getUnidadePK().getUf());
		}
		return indicadores;
	}

	public void setIndicadores(List<Indicador> indicadores) {
		this.indicadores = indicadores;
	}

	public List<Projeto> getProjetosVincular() {
		return projetosVincular;
	}

	public void setProjetosVincular(List<Projeto> projetosVincular) {
		this.projetosVincular = projetosVincular;
	}

	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

	public List<Projeto> getProjetosSelecionadosVincular() {
		return projetosSelecionadosVincular;
	}

	public void setProjetosSelecionadosVincular(List<Projeto> projetosSelecionadosVincular) {
		this.projetosSelecionadosVincular = projetosSelecionadosVincular;
	}

	public Projeto getProjetoSelecionado() {
		return projetoSelecionado;
	}

	public void setProjetoSelecionado(Projeto projetoSelecionado) {
		this.projetoSelecionado = projetoSelecionado;
	}

	public List<Unidade> getUnidadesVincular() {
		return unidadesVincular;
	}

	public void alterouUfFiltroUnidade() {
		if (this.ufUnidades != null) {
			this.unidadesVincular = unidadeService.findByUf(this.ufUnidades);
		} else {
			this.unidadesVincular = unidadeService.findAll();
		}
	}

	public void setUnidadesVincular(List<Unidade> unidadesVincular) {
		this.unidadesVincular = unidadesVincular;
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

	public MarcoCritico getMarcoCriticoSelecionado() {
		return marcoCriticoSelecionado;
	}

	public void setMarcoCriticoSelecionado(MarcoCritico marcoCriticoSelecionado) {
		this.marcoCriticoSelecionado = marcoCriticoSelecionado;
	}

	public boolean isVisualizando() {
		return visualizando;
	}

	public void setVisualizando(boolean visualizando) {
		this.visualizando = visualizando;
	}

	public List<Observacao> getObservacoesGerente() {
		if (observacoesGerente == null || observacoesGerente.isEmpty()) {
			if (meta.getObservacoes() != null && !meta.getObservacoes().isEmpty()) {
				observacoesGerente = Lists.newArrayList(Collections2.filter(meta.getObservacoes(),
						new Predicate<Observacao>() {
							@Override
							public boolean apply(Observacao obs) {
								return obs.getTipo() == TipoObservacao.G
										&& Perfil.COLABORADOR_CHAVE.equals(obs.getChavePerfilDestino())
										&& obs.getStatus() == StatusEnvio.E;
							}
						}));
			}
		}
		return observacoesGerente;
	}

	public void setObservacoesGerente(List<Observacao> observacoesGerente) {
		this.observacoesGerente = observacoesGerente;
	}

	public List<Observacao> getObservacoesComite() {
		if (observacoesComite == null || observacoesComite.isEmpty()) {
			if (meta.getObservacoes() != null && !meta.getObservacoes().isEmpty()) {
				observacoesComite = Lists.newArrayList(Collections2.filter(meta.getObservacoes(),
						new Predicate<Observacao>() {
							@Override
							public boolean apply(Observacao obs) {
								return obs.getTipo() == TipoObservacao.C
										&& Perfil.COLABORADOR_CHAVE.equals(obs.getChavePerfilDestino())
										&& obs.getStatus() == StatusEnvio.E;
							}
						}));
			}
		}
		return observacoesComite;
	}

	public void setObservacoesComite(List<Observacao> observacoesComite) {
		this.observacoesComite = observacoesComite;
	}

	public List<Observacao> getObservacoesUGP() {
		if (observacoesUGP == null || observacoesUGP.isEmpty()) {
			if (meta.getObservacoes() != null && !meta.getObservacoes().isEmpty()) {
				observacoesUGP = Lists.newArrayList(Collections2.filter(meta.getObservacoes(),
						new Predicate<Observacao>() {
							@Override
							public boolean apply(Observacao obs) {
								return obs.getTipo() == TipoObservacao.U
										&& Perfil.COLABORADOR_CHAVE.equals(obs.getChavePerfilDestino())
										&& obs.getStatus() == StatusEnvio.E;
							}
						}));
			}
		}

		return observacoesUGP;
	}

	public void setObservacoesUGP(List<Observacao> observacoesUGP) {
		this.observacoesUGP = observacoesUGP;
	}

	public void prepararModalVincularProjetos() {
		try {
			projetosVincular = projetoService.findAllByUf(this.appBean.getUfSelecionada());
			projetosSelecionadosVincular = new ArrayList<Projeto>();
			this.nomeBusca = "";
			editandoProjetos = false;
			if (meta.getProjetosVinculados() != null) {
				projetosVincular.removeAll(meta.getProjetosVinculados());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<Observacao> getObservacoesMonitoramento() {
		if (observacoesMonitoramento == null || observacoesMonitoramento.isEmpty()) {
			if (meta.getObservacoes() != null && !meta.getObservacoes().isEmpty()) {
				observacoesMonitoramento = Lists.newArrayList(Collections2.filter(meta.getObservacoes(),
						new Predicate<Observacao>() {
							@Override
							public boolean apply(Observacao obs) {
								return obs.getTipo() == TipoObservacao.M
										&& Perfil.COLABORADOR_CHAVE.equals(obs.getChavePerfilDestino())
										&& obs.getStatus() == StatusEnvio.E;
							}
						}));
			}
		}
		return observacoesMonitoramento;
	}

	public void setObservacoesMonitoramento(List<Observacao> observacoesMonitoramento) {
		this.observacoesMonitoramento = observacoesMonitoramento;
	}

	public void prepararModalVincularProjetosEditar() {
		try {
			projetosVincular = projetoService.findAllByUf(this.appBean.getUfSelecionada());
			projetosSelecionadosVincular = new ArrayList<Projeto>(this.meta.getProjetosVinculados());
			this.nomeBusca = "";
			editandoProjetos = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirProjeto() {
		try {
			this.meta.getProjetosVinculados().remove(this.projetoSelecionado);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarProjetosVincular() {
		try {
			this.projetosVincular = projetoService.findByNameLike(this.nomeBusca, appBean.getUfSelecionada());
			if (meta.getProjetosVinculados() != null) {
				projetosVincular.removeAll(meta.getProjetosVinculados());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formProjetosVincular",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void vincularProjetos() {
		try {
			if (editandoProjetos) {
				this.meta.setProjetosVinculados(projetosSelecionadosVincular);
			} else {
				if (this.meta.getProjetosVinculados() == null) {
					this.meta.setProjetosVinculados(new ArrayList<Projeto>());
				}
				List<Projeto> projetosVinculadosTemp = this.meta.getProjetosVinculados();
				projetosVinculadosTemp.addAll(this.projetosSelecionadosVincular);
				Collections.sort(projetosVinculadosTemp, new Comparator<Projeto>() {
					@Override
					public int compare(Projeto o1, Projeto o2) {
						return o1.getNome().compareTo(o2.getNome());
					}
				});
			}
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
			unidadesVincular = unidadeService.findAll();
			unidadesSelecionadasVincular = new ArrayList<Unidade>();
			this.nomeBusca = "";
			editandoUnidades = false;
			if (meta.getUnidadesVinculadas() != null) {
				unidadesVincular.removeAll(meta.getUnidadesVinculadas());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			this.ufUnidades = null;
			unidadesVincular = unidadeService.findAll();
			unidadesSelecionadasVincular = new ArrayList<Unidade>(this.meta.getUnidadesVinculadas());
			this.nomeBusca = "";
			editandoUnidades = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirUnidade() {
		try {
			this.meta.getUnidadesVinculadas().remove(this.unidadeSelecionada);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarUnidadesVincular() {
		try {
			this.unidadesVincular = unidadeService.findByDescricao(this.nomeBusca);
			if (meta.getUnidadesVinculadas() != null) {
				unidadesVincular.removeAll(meta.getUnidadesVinculadas());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formUnidadesVincular",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void vincularUnidades() {
		try {
			if (editandoUnidades) {
				this.meta.setUnidadesVinculadas(this.unidadesSelecionadasVincular);
			} else {
				if (this.meta.getUnidadesVinculadas() == null) {
					this.meta.setUnidadesVinculadas(new ArrayList<Unidade>());
				}
				List<Unidade> unidadesVinculadosTemp = this.meta.getUnidadesVinculadas();
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

	/*
	 * Marco critico
	 */
	public void prepararAdicionarMarcoCritico() {
		try {
			marcoCriticoSelecionado = new MarcoCritico();
			validarAdicaoMarcosCriticos();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
			FacesUtil.addErrorMessage("formMarcoCritico", ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void adicionarMarcoCritico() {
		try {
			validarAdicaoMarcosCriticos();
			if (meta.getMarcosCriticos() == null) {
				meta.setMarcosCriticos(new ArrayList<MarcoCritico>());
			}
			meta.getMarcosCriticos().add(marcoCriticoSelecionado);
			marcoCriticoSelecionado.setMeta(meta);
			this.marcoCriticoSelecionado.setEditando(Boolean.FALSE);
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
			FacesUtil.addErrorMessage("formMarcoCritico", ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("formMarcoCritico",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void salvarMarcoCritico() {
		this.marcoCriticoSelecionado.setEditando(Boolean.FALSE);
	}

	public void prepararEdicaoMarcoCritico() {
		this.marcoCriticoSelecionado.setEditando(Boolean.TRUE);
	}

	public void excluirMarcoCritico() {
		try {
			meta.getMarcosCriticos().remove(this.marcoCriticoSelecionado);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public boolean isPodeInserirMarcoCriticoCicloAtual() {
		if (meta != null) {
			CicloConfiguracao cc = this.appBean.getCicloConfiguracaoDesempenho();
			if (cc != null) {
				List<ConfiguracaoMarcoCritico> configuracoesMarcoCritico = configuracaoMarcoCriticoService
						.findByTipoUnidadeCicloConfiguracao(TipoMeta.I, appBean.getUsuarioAutenticado().getUnidade(),
								cc);

				if (configuracoesMarcoCritico != null && !configuracoesMarcoCritico.isEmpty()) {
					return configuracoesMarcoCritico.get(0).getDisponibilizadoInsercaoMetas();
				}
				if (configuracoesMarcoCritico.size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	private void validarAdicaoMarcosCriticos() throws ValidateException {
		CicloConfiguracao cc = this.appBean.getCicloConfiguracaoDesempenho();
		if (cc != null) {
			List<ConfiguracaoMarcoCritico> configuracoesMarcoCritico = configuracaoMarcoCriticoService
					.findByTipoUnidadeCicloConfiguracao(TipoMeta.I, appBean.getUsuarioAutenticado().getUnidade(), cc);

			if (configuracoesMarcoCritico != null && !configuracoesMarcoCritico.isEmpty()) {
				ConfiguracaoMarcoCritico cmc = configuracoesMarcoCritico.get(0);

				if (cmc.getQtdMarcosCriticos() != null && meta.getMarcosCriticos() != null) {
					if (cmc.getQtdMarcosCriticos().compareTo(meta.getMarcosCriticos().size()) <= 0) {
						throw new ValidateException(
								"O ciclo selecionado est\u00E1 configurado para permitir a inser\u00E7\u00E3o de no m\u00E1ximo "
										+ cmc.getQtdMarcosCriticos() + " marcos cr\u00EDticos");
					}
				}
			}

		}
	}

	public Observacao getObservacaoSelecionada() {
		return observacaoSelecionada;
	}

	public void setObservacaoSelecionada(Observacao observacaoSelecionada) {
		this.observacaoSelecionada = observacaoSelecionada;
	}

	public List<Object[]> getRegistrosLog() {
		return registrosLog;
	}

	public void setRegistrosLog(List<Object[]> registrosLog) {
		this.registrosLog = registrosLog;
	}

	private void salvar() throws ValidateException {
		validarSalvarMeta();
		this.meta.setCicloConfiguracao(this.appBean.getCicloConfiguracaoDesempenho());
		this.meta.setTipo(TipoMeta.I);

		if (meta.getId() == null) {
			TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.INC);
		} else {
			TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.EDI);
		}

		metaService.save(this.meta);
	}

	/*
	 * Meta
	 */
	public String salvarMeta() {
		try {
			this.meta.setStatusAtual(StatusMeta.SA);
			salvar();
			FacesUtil.addInfoMessage("Meta salva com sucesso");
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	private void validarSalvarMeta() throws ValidateException {
		List<String> erros = new ArrayList<String>();
		CicloConfiguracao cc = this.appBean.getCicloConfiguracaoDesempenho();
		if (cc != null) {
			List<ConfiguracaoMarcoCritico> configuracoesMarcoCritico = configuracaoMarcoCriticoService
					.findByTipoUnidadeCicloConfiguracao(TipoMeta.I, appBean.getUsuarioAutenticado().getUnidade(), cc);

			if (configuracoesMarcoCritico != null && !configuracoesMarcoCritico.isEmpty()) {
				ConfiguracaoMarcoCritico cmc = configuracoesMarcoCritico.get(0);
				if (cmc.getInsercaoObrigatoria()) {
					if (this.meta.getMarcosCriticos() == null) {
						erros.add("\u00C9 necess\u00E1rio inserir no m\u00EDnimo um marco critico para as metas deste ciclo.");
					} else if (this.meta.getMarcosCriticos().size() < cmc.getQtdMarcosCriticos()) {
						erros.add("O ciclo selecionado est\u00E1 configurado para permitir a inser\u00E7\u00E3o de no m\u00EDnimo " + cmc.getQtdMarcosCriticos()
								+ " marcos cr\u00EDticos");
					}
				}
			}
		}
		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	public String excluir() {
		try {
			if (appBean.getCicloSelecionado().getStatusRepactuacao(TipoConfiguracaoCiclo.DESEMP) == StatusCiclo.I) {
				FacesUtil.addErrorMessage("A meta n\u00E3o pode ser exclu\u00EDda, pois se encontra na fase de pactua\u00E7\u00E3o.");
			} else {
				this.metaService.delete(this.meta);
				this.listarMetasBean.atualizarListagem();
				FacesUtil.addInfoMessage("Meta exclu\u00EDda com sucesso.");
				return appBean.back();
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Esta meta n\u00E3o pode ser exclu\u00EDda, pois possui registros associados a ela.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String enviarAprovacao() {
		try {
			FluxoAprovacaoMetas fluxo = this.meta.getCicloConfiguracao().getFluxoByUnidade(meta.getUnidade());
			if (fluxo == null) {
				FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade " + meta.getUnidade().getDescricao());
			} else {
				salvar();
				this.metaService.enviarMetaAprovacao(meta, meta.getCicloConfiguracao().getTipoConfiguracao(), TipoGrupo.C);
				FacesUtil.addInfoMessage("Meta enviada para aprova\u00E7\u00E3o com sucesso!");
				if (pendenciasBean != null) {
					pendenciasBean.removerMeta(meta);
				}
				return appBean.back();
			}
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar opera\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String cancelarMeta() {
		try {
			if (this.meta.getStatusByFase(Fase.R) != null) {
				this.meta.getMetaStatusByFase(Fase.R).setStatus(StatusMeta.CS);

			} else {
				this.meta.getMetaStatusByFase(Fase.P).setStatus(StatusMeta.CS);
			}
			this.metaService.save(meta);
			FacesUtil.addInfoMessage("Meta cancelada com sucesso.");
			return appBean.back();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public boolean verificarStatusByFase() {
		if (appBean.getCicloSelecionado().getStatusRepactuacao(TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C) == StatusCiclo.I) {
			return true;
		} else {
			return false;
		}
	}

	public void enviarResposta() {
		try {
			this.metaService.enviarResposta(this.observacaoSelecionada, this.meta);
			if (pendenciasBean != null) {
				pendenciasBean.removerMeta(meta);
			}
			FacesUtil.addInfoMessage("Resposta enviada com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formObsResposta",
					"Erro ao executar opera\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void obterRegistrosLog() {
		try {
			this.registrosLog = this.logService.findByIdMeta(this.meta.getId());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar opera\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public TipoCampoObservacao getTipoCampoObservacao() {
		return tipoCampoObservacao;
	}

	public void setTipoCampoObservacao(TipoCampoObservacao tipoCampoObservacao) {
		this.tipoCampoObservacao = tipoCampoObservacao;
	}

	public UF getUfUnidades() {
		return ufUnidades;
	}

	public void setUfUnidades(UF ufUnidades) {
		this.ufUnidades = ufUnidades;
	}

}
