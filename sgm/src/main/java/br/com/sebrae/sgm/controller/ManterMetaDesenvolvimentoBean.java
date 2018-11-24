package br.com.sebrae.sgm.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Anexo;
import br.com.sebrae.sgm.model.Categoria;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.FluxoAprovacaoMetas;
import br.com.sebrae.sgm.model.FormaAquisicao;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.MetaStatus;
import br.com.sebrae.sgm.model.ObjetivoEstrategico;
import br.com.sebrae.sgm.model.RMCompetencia;
import br.com.sebrae.sgm.model.SolucaoEducacional;
import br.com.sebrae.sgm.model.SolucaoEducacionalMeta;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.audit.listener.TipoAlteracaoTheadLocal;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoCargaHoraria;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoLog;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.VinculoOcupacional;
import br.com.sebrae.sgm.service.CategoriaService;
import br.com.sebrae.sgm.service.FormaAquisicaoService;
import br.com.sebrae.sgm.service.LogService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.ObjetivoEstrategicoService;
import br.com.sebrae.sgm.service.RMCompetenciaService;
import br.com.sebrae.sgm.service.SolucaoEducacionalMetaService;
import br.com.sebrae.sgm.service.SolucaoEducacionalService;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ManterMetaDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ManterMetaDesenvolvimentoBean.class);

	@Inject
	private AppBean appBean;
	@Inject
	private MetaService metaService;
	@Inject
	private CategoriaService categoriaService;
	@Inject
	private FormaAquisicaoService formaAquisicaoService;
	@Inject
	private RMCompetenciaService competenciaService;
	@Inject
	private ObjetivoEstrategicoService objetivoEstrategicoService;
	@Inject
	private SolucaoEducacionalService solucaoEducacionalService;
	@Inject
	private SolucaoEducacionalMetaService solucaoEducacionalMetaService;

	@Inject
	private PendenciasBean pendenciaBean;

	@Inject
	private LogService logService;

	private List<Meta> metasEquipeVincular = new ArrayList<Meta>();
	private List<Meta> metasIndividualVincular = new ArrayList<Meta>();
	private List<RMCompetencia> competenciasVincular = new ArrayList<RMCompetencia>();
	private List<ObjetivoEstrategico> objetivosEstrategicosVincular = new ArrayList<ObjetivoEstrategico>();

	private Meta meta;

	private SolucaoEducacionalMeta solucaoSelecionada;
	private Categoria categoria;
	private List<Categoria> categorias;
	private FormaAquisicao formaAquisicao;
	private List<FormaAquisicao> formasAquisicao = new ArrayList<FormaAquisicao>();

	private boolean visualizandoSolucao = false;
	private boolean editandoSolucao = false;
	private SolucaoEducacionalMeta solucaoEducacionalEditando;

	private int indiceAnexo;
	private boolean visualizando;

	private List<Object[]> registrosLog;

	@Override
	public void init() {
		super.init();
		if (this.meta == null) {
			this.meta = new Meta();
			this.meta.setColaborador((Usuario) appBean.getUsuarioAutenticado());
			this.meta.setStatus(new ArrayList<MetaStatus>());
			br.com.sebrae.sgm.model.MetaStatus ms = new MetaStatus();
			ms.setFase(Fase.P);
			ms.setStatus(StatusMeta.SA);
			ms.setMeta(this.meta);
			this.meta.adicionarMetaStatus(ms);
			if (this.appBean.getUsuarioAutenticado().getUnidades() != null && !this.appBean.getUnidadesUf().isEmpty()) {
				this.meta.setUnidade(this.appBean.getUnidadesUf().get(0));
			}
			this.meta.setMetasInviduaisEquipeVinculadas(new ArrayList<Meta>());
			this.meta.setCompetencias(new ArrayList<RMCompetencia>());
			this.meta.setObjetivosEstrategicos(new ArrayList<ObjetivoEstrategico>());
			this.meta.setCicloConfiguracao(this.appBean.getCicloConfiguracaoDesenvolvimento());
			this.meta.setColaborador(this.appBean.getUsuarioAutenticado());
		} else {
			getMetasEquipeVincular();
			getMetasIndividualVincular();

			if (this.meta.getMetasInviduaisEquipeVinculadas() != null
					&& !this.meta.getMetasInviduaisEquipeVinculadas().isEmpty()
					&& (this.metasEquipeVincular != null || this.metasIndividualVincular != null)) {
				List<Meta> metasIndividualEquipeVinculadas = this.meta.getMetasInviduaisEquipeVinculadas();

				for (Meta meta : metasIndividualEquipeVinculadas) {
					if (metasEquipeVincular != null)
						if (metasEquipeVincular.contains(meta)) {
							metasEquipeVincular.get(metasEquipeVincular.indexOf(meta)).setSelecionado(Boolean.TRUE);
						}
					if (metasIndividualVincular != null)
						if (metasIndividualVincular.contains(meta)) {
							metasIndividualVincular.get(metasIndividualVincular.indexOf(meta)).setSelecionado(
									Boolean.TRUE);
						}
				}
			}

			getCompetenciasVincular();
			if (this.meta.getCompetencias() != null && !this.meta.getCompetencias().isEmpty()
					&& this.competenciasVincular != null && !this.competenciasVincular.isEmpty()) {
				List<RMCompetencia> competencias = this.meta.getCompetencias();
				for (RMCompetencia rmCompetencia : competencias) {
					if (this.competenciasVincular.contains(rmCompetencia)) {
						this.competenciasVincular.get(competenciasVincular.indexOf(rmCompetencia)).setSelecionado(
								Boolean.TRUE);
					}
				}
			}

			getObjetivosEstrategicosVincular();

			if (this.meta.getObjetivosEstrategicos() != null && !this.meta.getObjetivosEstrategicos().isEmpty()
					&& this.objetivosEstrategicosVincular != null && !this.objetivosEstrategicosVincular.isEmpty()) {
				List<ObjetivoEstrategico> objetivosEstrategicos = this.meta.getObjetivosEstrategicos();

				for (ObjetivoEstrategico objetivoEstrategico : objetivosEstrategicos) {
					if (this.objetivosEstrategicosVincular.contains(objetivoEstrategico)) {
						this.objetivosEstrategicosVincular.get(
								objetivosEstrategicosVincular.indexOf(objetivoEstrategico))
								.setSelecionado(Boolean.TRUE);
					}
				}
			}
		}
	}

	private void ajustesMetaPreSalvar() {
		if (this.metasEquipeVincular != null && !this.metasEquipeVincular.isEmpty()) {
			for (Meta m : this.metasEquipeVincular) {
				if (m.getSelecionado()) {
					if (!this.getMeta().getMetasInviduaisEquipeVinculadas().contains(m)) {
						this.getMeta().getMetasInviduaisEquipeVinculadas().add(m);
					}
				} else {
					if (this.getMeta().getMetasInviduaisEquipeVinculadas().contains(m)) {
						this.getMeta().getMetasInviduaisEquipeVinculadas().remove(m);
					}
				}
			}
		}

		if (this.metasIndividualVincular != null && !this.metasIndividualVincular.isEmpty()) {
			for (Meta m : this.metasIndividualVincular) {
				if (m.getSelecionado()) {
					if (!this.getMeta().getMetasInviduaisEquipeVinculadas().contains(m)) {
						this.getMeta().getMetasInviduaisEquipeVinculadas().add(m);
					}
				} else {
					if (this.getMeta().getMetasInviduaisEquipeVinculadas().contains(m)) {
						this.getMeta().getMetasInviduaisEquipeVinculadas().remove(m);
					}
				}
			}
		}

		if (this.competenciasVincular != null && !this.competenciasVincular.isEmpty()) {
			for (RMCompetencia c : this.competenciasVincular) {
				if (c.getSelecionado()) {
					if (!this.getMeta().getCompetencias().contains(c)) {
						this.getMeta().getCompetencias().add(c);
					}
				} else {
					if (this.getMeta().getCompetencias().contains(c)) {
						this.getMeta().getCompetencias().remove(c);
					}
				}
			}
		}

		if (this.objetivosEstrategicosVincular != null && !this.objetivosEstrategicosVincular.isEmpty()) {
			for (ObjetivoEstrategico oe : this.objetivosEstrategicosVincular) {
				if (oe.getSelecionado()) {
					if (!this.getMeta().getObjetivosEstrategicos().contains(oe)) {
						this.getMeta().getObjetivosEstrategicos().add(oe);
					}
				} else {
					if (this.getMeta().getObjetivosEstrategicos().contains(oe)) {
						this.getMeta().getObjetivosEstrategicos().remove(oe);
					}
				}
			}
		}
	}

	public String salvarMeta() {
		try {
			super.conversation.setTimeout(18000);
			ajustesMetaPreSalvar();
			validarSalvarMeta();
			meta.setStatusAtual(StatusMeta.SA);
			if (meta.getCicloConfiguracao() == null) {
				this.meta.setCicloConfiguracao(this.appBean.getCicloConfiguracaoDesenvolvimento());
			}
			if (this.meta.getTipo() == null) {
				this.meta.setTipo(TipoMeta.D);
			}

			if (meta.getId() == null) {
				TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.INC);
			} else {
				TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.EDI);
			}

			metaService.save(this.meta);
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
		CicloConfiguracao c = this.appBean.getCicloConfiguracaoDesempenho();

		/*
		 * if (!isPossuiMetaEquipeSelecionada()) {
		 * FacesUtil.addErrorMessage("form_meta_desenvolvimento:tbl_metas_equipe_vincular",
		 * "\u00C9 necess\u00E1rio vincular no m\u00EDnimo uma meta de equipe a meta de desenvolvimento.");
		 * erros.add("\u00C9 necess\u00E1rio vincular no m\u00EDnimo uma meta de equipe a meta de desenvolvimento."); }
		 *
		 * if (!isPossuiMetaIndividualSelecionada()) {
		 * FacesUtil.addErrorMessage("form_meta_desenvolvimento:tbl_metas_individual_vincular",
		 * "\u00C9 necess\u00E1rio vincular no m\u00EDnimo uma meta individual a meta de desenvolvimento.");
		 * erros.add("\u00C9 necess\u00E1rio vincular no m\u00EDnimo uma meta individual a meta de desenvolvimento."); }
		 *
		 * if (!isPossuiCompetenciaSelecionada()) {
		 * FacesUtil.addErrorMessage("form_meta_desenvolvimento:tbl_competencias_vincular",
		 * "\u00C9 necess\u00E1rio vincular no m\u00EDnimo uma compet\u00EAncia a meta de desenvolvimento.");
		 * erros.add("\u00C9 necess\u00E1rio vincular no m\u00EDnimo uma compet\u00EAncia a meta de desenvolvimento.");
		 * }
		 *
		 * if (!isPossuiObjetivoEstrategicoSelecionado()) { FacesUtil
		 * .addErrorMessage("form_meta_desenvolvimento:tbl_objetivos_estrategicos_unidade",
		 * "\u00C9 necess\u00E1rio vincular no m\u00EDnimo um objetivo estrat\u00E9gico a meta de desenvolvimento.");
		 * erros
		 * .add("\u00C9 necess\u00E1rio vincular no m\u00EDnimo um objetivo estrat\u00E9gico a meta de desenvolvimento."
		 * ); }
		 */

		if (!isPossuiMetaEquipeSelecionada() && !isPossuiMetaIndividualSelecionada()
				&& !isPossuiCompetenciaSelecionada() && !isPossuiObjetivoEstrategicoSelecionado()) {
			erros.add("\u00C9 necess\u00E1rio ter no m\u00EDnimo um v\u00EDnculo para grava\u00E7\u00E3o da meta");
		}

		if (this.meta.getSolucoesEducacionais() == null || this.meta.getSolucoesEducacionais().isEmpty()) {
			FacesUtil
					.addErrorMessage("form_meta_desenvolvimento:tbl_solucoes_educationais",
							"\u00C9 necess\u00E1rio adicionar no m\u00EDnimo uma solu\u00E7\u00E3o educacional a meta de desenvolvimento.");
			erros.add("\u00C9 necess\u00E1rio adicionar no m\u00EDnimo uma solu\u00E7\u00E3o educacional a meta de desenvolvimento.");
		}

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	public String enviarAprovacao() {
		try {
			ajustesMetaPreSalvar();
			validarSalvarMeta();
			FluxoAprovacaoMetas fluxo = this.meta.getCicloConfiguracao().getFluxoByUnidade(meta.getUnidade());
			if (fluxo == null) {
				FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade " + meta.getUnidade().getDescricao());
			} else {
				if (meta.getCicloConfiguracao() == null) {
					this.meta.setCicloConfiguracao(this.appBean.getCicloConfiguracaoDesenvolvimento());
				}
				if (this.meta.getTipo() == null) {
					this.meta.setTipo(TipoMeta.D);
				}
				metaService.save(this.meta);
				metaService.enviarMetaAprovacao(this.meta, TipoConfiguracaoCiclo.DESENV, TipoGrupo.C);
				FacesUtil.addInfoMessage("Meta enviada para aprova\u00E7\u00E3o com sucesso.");
				if (pendenciaBean != null) {
					pendenciaBean.removerMeta(this.meta);
				}
				return appBean.back();
			}
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public void excluirSolucao() {
		try {
			this.meta.getSolucoesEducacionais().remove(this.solucaoSelecionada);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	private boolean isPossuiMetaEquipeSelecionada() {
		if (this.metasEquipeVincular != null) {
			for (Meta m : this.metasEquipeVincular) {
				if (m.getSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPossuiMetaIndividualSelecionada() {
		if (this.metasIndividualVincular != null) {
			for (Meta m : this.metasIndividualVincular) {
				if (m.getSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPossuiCompetenciaSelecionada() {
		if (this.competenciasVincular != null) {
			for (RMCompetencia c : this.competenciasVincular) {
				if (c.getSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPossuiObjetivoEstrategicoSelecionado() {
		if (this.objetivosEstrategicosVincular != null) {
			for (ObjetivoEstrategico obj : this.objetivosEstrategicosVincular) {
				if (obj.getSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}

	public void prepararAdicaoSolucaoEducacional() {
		this.solucaoSelecionada = new SolucaoEducacionalMeta();
		this.solucaoSelecionada.setSolucaoEducacional(new SolucaoEducacional());
		this.solucaoSelecionada.setMeta(this.meta);
		this.categoria = null;
		this.formasAquisicao = new ArrayList<FormaAquisicao>();
		this.formaAquisicao = null;
	}

	public void prepararAlteracaoSolucaoEducacional() {
		try {
			this.categoria = this.getSolucaoEducacionalEditando().getSolucaoEducacional().getFormaAquisicao()
					.getCategoria();
			this.formaAquisicao = this.getSolucaoEducacionalEditando().getSolucaoEducacional().getFormaAquisicao();
			this.solucaoSelecionada = this.getSolucaoEducacionalEditando().clone();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void prepararVisualizacaoSolucaoEducacional() {
		try {
			this.categoria = this.getSolucaoSelecionada().getSolucaoEducacional().getFormaAquisicao().getCategoria();
			this.formaAquisicao = this.getSolucaoSelecionada().getSolucaoEducacional().getFormaAquisicao();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void adicionarSolucaoEducacional() {
		try {
			validarAdicaoSolucao();
			if (this.meta.getSolucoesEducacionais() == null) {
				this.meta.setSolucoesEducacionais(new ArrayList<SolucaoEducacionalMeta>());
			}
			if (editandoSolucao) {
				this.solucaoEducacionalMetaService.delete(getSolucaoEducacionalEditando());
				this.meta.getSolucoesEducacionais().remove(getSolucaoEducacionalEditando());
				this.solucaoEducacionalEditando = null;
			}

			if (this.solucaoSelecionada.getSolucaoEducacional().getQuantidade() == null) {
				if (this.solucaoSelecionada.getQuantidadePrevista() != null) {
					this.solucaoSelecionada.getSolucaoEducacional().setQuantidade(this.solucaoSelecionada.getQuantidadePrevista());
				} else {
					this.solucaoSelecionada.getSolucaoEducacional().setQuantidade(new BigDecimal(0));
				}
			}
			if (this.solucaoSelecionada.getSolucaoEducacional().getId() == null) {
				this.solucaoEducacionalService.save(this.solucaoSelecionada.getSolucaoEducacional());
			}
			this.meta.getSolucoesEducacionais().add(this.solucaoSelecionada);
			this.editandoSolucao = false;
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage("form_solucao_educacional", ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_solucao_educacional", e.getMessage());
		}

	}

	public void validarAdicaoSolucao() throws ValidateException {
		List<String> erros = new ArrayList<String>();

		if (this.solucaoSelecionada.getVinculoOcupacional() == null) {
			erros.add("Informe o vinculo ocupacional.");
			FacesUtil.addErrorMessage("form_solucao_educacional:vinculoOcupacional", "Informe o vinculo ocupacional.");
		}

		/*if (this.solucaoSelecionada.getQuantidadePrevista() == null) {
			erros.add("Informe a quantidade da solu\u00E7\u00E3o educacional.");
			FacesUtil.addErrorMessage("form_solucao_educacional:quantidade",
					"Informe a quantidade da solu\u00E7\u00E3o educacional.");
		}*/

		if (this.formaAquisicao == null) {
			erros.add("Informe a forma de aquisi\u00E7\u00E3o.");
			FacesUtil.addErrorMessage("form_solucao_educacional:formaAquisicao",
					"Informe a forma de aquisi\u00E7\u00E3o.");
		} else {
			if (this.formaAquisicao.getTipoCargaHoraria() == TipoCargaHoraria.U) {
				if (this.solucaoSelecionada.getSolucaoEducacional() == null
						|| this.solucaoSelecionada.getSolucaoEducacional().getId() == null) {
					erros.add("Informe a solu\u00E7\u00E3o educacional.");
					FacesUtil.addErrorMessage("form_solucao_educacional:solucaoEducacional",
							"Informe a solu\u00E7\u00E3o educacional.");
				}
			} else {
				if (StringUtils.isBlank(this.solucaoSelecionada.getSolucaoEducacional().getNome())) {
					erros.add("Informe o nome da solu\u00E7\u00E3o educacional.");
					FacesUtil.addErrorMessage("form_solucao_educacional:nomeSolucao",
							"Informe o nome da solu\u00E7\u00E3o educacional.");
				}
			}
		}

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}

	}

	public void alterouCategoria() {
		this.formasAquisicao = new ArrayList<FormaAquisicao>();
		this.formaAquisicao = null;
		if (this.solucaoSelecionada != null)
			this.solucaoSelecionada.setSolucaoEducacional(new SolucaoEducacional());
	}

	public void alterouFormaAquisicao() {
		if (this.solucaoSelecionada != null)
			this.solucaoSelecionada.setSolucaoEducacional(new SolucaoEducacional());

		if (this.formaAquisicao != null) {
			if (this.solucaoSelecionada != null && this.solucaoSelecionada.getSolucaoEducacional() != null) {
				this.solucaoSelecionada.getSolucaoEducacional().setMedidaBase(this.formaAquisicao.getUnidadeMedida());
				this.solucaoSelecionada.getSolucaoEducacional().setFormaAquisicao(this.formaAquisicao);
			}

			if (this.formaAquisicao.getTipoCargaHoraria() == TipoCargaHoraria.F) {
				this.solucaoSelecionada.setQuantidadePrevista(this.formaAquisicao.getCargaHoraria());
			}
		}
	}

	public void alterouSolucaoEducacional() {
		if (this.solucaoSelecionada.getSolucaoEducacional() != null) {
			this.solucaoSelecionada.setQuantidadePrevista(this.solucaoSelecionada.getSolucaoEducacional()
					.getQuantidade());
		}
	}

	public void uploadArquivo(FileUploadEvent event) {
		try {
			String fileName = FilenameUtils.getName(event.getFile().getFileName());
			if (this.getSolucaoSelecionada().getAnexos() == null) {
				this.getSolucaoSelecionada().setAnexos(new ArrayList<Anexo>());
			}
			Anexo an = new Anexo();
			an.setNome(RandomStringUtils.random(8, true, true) + fileName);
			an.setNomeExibicao(fileName);
			byte[] fileBytes = IOUtils.toByteArray(event.getFile().getInputstream());
			an.setTipo(event.getFile().getContentType());
			an.setArquivo(fileBytes);
			FileUtils
					.writeByteArrayToFile(new File(Constants.FILES_TMP_DIR + File.separator + an.getNome()), fileBytes);
			an.setSolucaoEducacionalMeta(this.solucaoSelecionada);
			this.solucaoSelecionada.getAnexos().add(an);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_anexo", "Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirArquivo() {
		this.getSolucaoSelecionada().getAnexos().remove(this.indiceAnexo);
	}

	public StreamedContent getArquivo() {
		StreamedContent file = null;

		try {
			Anexo a = this.getSolucaoSelecionada().getAnexos().get(indiceAnexo);
			if (a.getArquivo() != null) {
				ByteArrayInputStream fis;
				fis = new ByteArrayInputStream(a.getArquivo());
				file = new DefaultStreamedContent(fis, a.getTipo(), a.getNome());
			} else {
				FacesUtil.addErrorMessage("Nenhum arquivo a ser exibido.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}

		return file;
	}

	public StreamedContent getArquivo2() {
		StreamedContent file = null;
		try {
			Anexo a = appBean.getCicloConfiguracaoDesenvolvimento().getConfiguracaoFormaAquisicao().getAnexos()
					.get(indiceAnexo);
			if (a.getArquivo() != null) {
				ByteArrayInputStream fis;
				fis = new ByteArrayInputStream(a.getArquivo());
				file = new DefaultStreamedContent(fis, a.getTipo(), a.getNome());
			} else {
				FacesUtil.addErrorMessage("Nenhum arquivo a ser exibido.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return file;
	}

	public void salvarAnexos() {
		try {
			if (this.solucaoSelecionada.getSolucaoEducacionalMetaPK().getIdMeta() != null
					&& this.solucaoSelecionada.getSolucaoEducacionalMetaPK().getIdSolucaoEducacional() != null) {
				this.solucaoEducacionalMetaService.atualizar(this.solucaoSelecionada);
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
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

	public List<VinculoOcupacional> getVinculosOcupacionais() {
		return Arrays.asList(VinculoOcupacional.values());
	}

	public List<RMCompetencia> getCompetenciasVincular() {
		if (this.competenciasVincular == null || this.competenciasVincular.isEmpty()) {
			this.competenciasVincular = competenciaService.findAllCompetenciaPorColaborador(this.appBean.getColaboradorSelecionado(), this.appBean
					.getCicloConfiguracaoDesenvolvimento().getCiclo().getVigencia());
		}
		return competenciasVincular;
	}

	public void setCompetenciasVincular(List<RMCompetencia> competenciasVincular) {
		this.competenciasVincular = competenciasVincular;
	}

	public List<ObjetivoEstrategico> getObjetivosEstrategicosVincular() {
		if (this.objetivosEstrategicosVincular == null || this.objetivosEstrategicosVincular.isEmpty()) {
			this.objetivosEstrategicosVincular = this.objetivoEstrategicoService.findAll();
		}
		return objetivosEstrategicosVincular;
	}

	public void setObjetivosEstrategicosVincular(List<ObjetivoEstrategico> objetivosEstrategicosVincular) {
		this.objetivosEstrategicosVincular = objetivosEstrategicosVincular;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public List<Meta> getMetasEquipeVincular() {
		if (this.metasEquipeVincular == null || this.metasEquipeVincular.isEmpty()) {
			this.metasEquipeVincular = metaService.findAllEquipeByCicloMetaAprovada(this.appBean.getCicloSelecionado());
		}
		return metasEquipeVincular;
	}

	public void setMetasEquipeVincular(List<Meta> metasEquipeVincular) {
		this.metasEquipeVincular = metasEquipeVincular;
	}

	public List<Meta> getMetasIndividualVincular() {
		if (this.metasIndividualVincular == null || this.metasIndividualVincular.isEmpty()) {
			this.metasIndividualVincular = metaService.findAllIndividuaisCicloAprovada(appBean.getUsuarioAutenticado(),
					appBean.getCicloSelecionado());
		}
		return metasIndividualVincular;
	}

	public void setMetasIndividualVincular(List<Meta> metasIndividualVincular) {
		this.metasIndividualVincular = metasIndividualVincular;
	}

	public SolucaoEducacionalMeta getSolucaoSelecionada() {
		return solucaoSelecionada;
	}

	public void setSolucaoSelecionada(SolucaoEducacionalMeta solucaoSelecionada) {
		this.solucaoSelecionada = solucaoSelecionada;
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

	public List<FormaAquisicao> getFormasAquisicao() {
		this.formasAquisicao = new ArrayList<FormaAquisicao>();
		if (categoria != null && (formasAquisicao == null || formasAquisicao.isEmpty())) {
			formasAquisicao = formaAquisicaoService.findByCategoria(this.categoria);
		}
		return formasAquisicao;
	}

	public List<SolucaoEducacional> getSolucoesEducacionais() {
		List<SolucaoEducacional> retorno = new ArrayList<SolucaoEducacional>();
		if (this.formaAquisicao != null) {
			retorno = this.solucaoEducacionalService.findByFormaAquisicao(this.formaAquisicao);
		}
		return retorno;
	}

	public void setFormasAquisicao(List<FormaAquisicao> formasAquisicao) {
		this.formasAquisicao = formasAquisicao;
	}

	public boolean isVisualizandoSolucao() {
		return visualizandoSolucao;
	}

	public void setVisualizandoSolucao(boolean visualizandoSolucao) {
		this.visualizandoSolucao = visualizandoSolucao;
	}

	public int getIndiceAnexo() {
		return indiceAnexo;
	}

	public void setIndiceAnexo(int indiceAnexo) {
		this.indiceAnexo = indiceAnexo;
	}

	public boolean isVisualizando() {
		return visualizando;
	}

	public void setVisualizando(boolean visualizando) {
		this.visualizando = visualizando;
	}

	public FormaAquisicao getFormaAquisicao() {
		return formaAquisicao;
	}

	public void setFormaAquisicao(FormaAquisicao formaAquisicao) {
		this.formaAquisicao = formaAquisicao;
	}

	public boolean isEditandoSolucao() {
		return editandoSolucao;
	}

	public void setEditandoSolucao(boolean editandoSolucao) {
		this.editandoSolucao = editandoSolucao;
	}

	public SolucaoEducacionalMeta getSolucaoEducacionalEditando() {
		return solucaoEducacionalEditando;
	}

	public void setSolucaoEducacionalEditando(SolucaoEducacionalMeta solucaoEducacionalEditando) {
		this.solucaoEducacionalEditando = solucaoEducacionalEditando;
	}

	public List<Object[]> getRegistrosLog() {
		return registrosLog;
	}

	public void setRegistrosLog(List<Object[]> registrosLog) {
		this.registrosLog = registrosLog;
	}

}
