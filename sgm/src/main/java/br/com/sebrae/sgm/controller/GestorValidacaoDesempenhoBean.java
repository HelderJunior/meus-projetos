package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoMarcoCritico;
import br.com.sebrae.sgm.model.Entrega;
import br.com.sebrae.sgm.model.Indicador;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.MetaStatus;
import br.com.sebrae.sgm.model.ObservacaoCampo;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.FormaCalculo;
import br.com.sebrae.sgm.model.enums.Mes;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoCampoObservacao;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoDado;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoObservacao;
import br.com.sebrae.sgm.service.ConfiguracaoMarcoCriticoService;
import br.com.sebrae.sgm.service.IndicadorService;
import br.com.sebrae.sgm.service.LogService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class GestorValidacaoDesempenhoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory
			.getLogger(GestorValidacaoDesempenhoBean.class);

	@Inject
	private AppBean appBean;
	@Inject
	private MetaService metaService;
	@Inject
	private LogService logService;
	@Inject
	private IndicadorService indicadorService;
	@Inject
	private GestorGestaoMetasDesempenhoNomeBean gestorGestaoMetasDesempenhoNomeBean;
	@Inject
	private GestaoMetasBean gestaoMetasBean;
	@Inject
	private ConfiguracaoMarcoCriticoService configuracaoMarcoCriticoService;
	@Inject
	private PendenciasBean pendenciasBean;


	private TipoCampoObservacao tipoCampoObservacao;
	private Meta meta;

	private String textoObservacao;
	private String textoObservacaoCampo;

	private List<Object[]> registrosLog;
	private List<Indicador> indicadores = new ArrayList<Indicador>();

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

			this.meta.setEntregas(new ArrayList<Entrega>());

			for (Mes m : Mes.values()) {
				meta.getEntregas().add(new Entrega(m, this.meta));
			}

			if (this.appBean.getUsuarioAutenticado().getUnidades() != null
					&& !this.appBean.getUnidadesUf().isEmpty()) {
				this.meta.setUnidade(this.appBean.getUnidadesUf().get(0));
			}
		}
	}

	public String salvarSemValidar() {
		try {
			this.metaService.save(this.meta);
			FacesUtil.addInfoMessage("Meta salva com sucesso.");
			return appBean.back();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a opera��oo, tente novamente mais tarde.");
		}
		return "";
	}

	public String enviarCorrecao() {
		try {
			this.metaService.reprovarMeta(meta, meta.getCicloConfiguracao().getTipoConfiguracao(), TipoGrupo.C);
			FacesUtil.addInfoMessage("Meta enviada para corre\u00E7\u00E3o com sucesso.");
			if (gestorGestaoMetasDesempenhoNomeBean != null && gestorGestaoMetasDesempenhoNomeBean.getMetas() != null) {
				gestorGestaoMetasDesempenhoNomeBean.getMetas().remove(meta);
			}
			if (gestorGestaoMetasDesempenhoNomeBean != null && gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto() != null) {
				if (gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao() != null
						&& !gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().isEmpty()) {
					gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().remove(meta);
					gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().setQtdPendenteAprovacao(
							gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getQtdPendenteAprovacao() - 1);
					gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao()
							.removeAll(this.meta.getSolucoesEducacionais());
					gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(
							gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size()
									- this.meta.getSolucoesEducacionais().size());

					if(gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size() < 0){
						gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(0);
					}
				}
			}
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a operação, tente novamente mais tarde.");
		}
		return "";
	}


	public String validarResultado() {
		try {
			this.meta.setCicloConfiguracao(appBean
					.getCicloConfiguracaoDesempenho());
			this.metaService.aprovarMeta(meta, meta.getCicloConfiguracao()
					.getTipoConfiguracao(), TipoGrupo.C);
			FacesUtil.addInfoMessage("Meta aprovada com sucesso.");
			gestaoMetasBean.resetCampos();
			gestorGestaoMetasDesempenhoNomeBean.removerMeta(this.meta);
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil
					.addErrorMessage("Erro ao executar a operação, tente novamente mais tarde.");
		}
		return "";
	}

	public String naoValidar() {
		try {
			this.metaService.reprovarMeta(meta, meta.getCicloConfiguracao().getTipoConfiguracao(), TipoGrupo.C);
			FacesUtil.addInfoMessage("Meta n\u00E3o validada com sucesso.");
			if (gestorGestaoMetasDesempenhoNomeBean != null && gestorGestaoMetasDesempenhoNomeBean.getMetas() != null) {
				gestorGestaoMetasDesempenhoNomeBean.getMetas().remove(meta);
			}
			if (gestorGestaoMetasDesempenhoNomeBean != null && gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto() != null) {
				if (gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao() != null
						&& !gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().isEmpty()) {
					gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().remove(meta);
					gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().setQtdPendenteAprovacao(
							gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getQtdPendenteAprovacao() - 1);
					gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao()
							.removeAll(this.meta.getSolucoesEducacionais());
					gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(
							gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size()
									- this.meta.getSolucoesEducacionais().size());

					if(gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size() < 0){
						gestorGestaoMetasDesempenhoNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(0);
					}
				}
			}
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a operação, tente novamente mais tarde.");
		}
		return "";
	}


	public String enviarObservacao() {
		try {
			if (StringUtils.isBlank(this.getTextoObservacao())) {
				FacesUtil
						.addErrorMessage(
								"form_validacao_meta:txObservacao",
								"O campo Observa\u00E7\u00F5es do Gestor \u00E9 de preenchimento obrigat\u00F3rio.");
				throw new ValidateException(
						"O campo Observa\u00E7\u00F5es do Gestor \u00E9 de preenchimento obrigat\u00F3rio.");
			}
			this.meta.setCicloConfiguracao(appBean
					.getCicloConfiguracaoDesempenho());
			metaService.enviarObservacaoSuperiorMeta(this.meta,
					getTextoObservacao());
			this.textoObservacao = null;
			gestaoMetasBean.resetCampos();
			gestorGestaoMetasDesempenhoNomeBean.removerMeta(this.meta);
			FacesUtil
					.addInfoMessage("Observa\u00E7\u00E3o enviada com sucesso!");
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public void obterRegistrosLog() {
		try {
			this.registrosLog = this.logService.findByIdMeta(this.meta.getId());
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar opera\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void adicioanrObservacaoCampo() {
		try {
			/*if (StringUtils.isBlank(this.getTextoObservacao())) {
				FacesUtil
						.addErrorMessage("frmObservacao:txObservacao",
								"O campo Observa\u00E7\u00F5es \u00E9 de preenchimento obrigat\u00F3rio.");
				throw new ValidateException(
						"O campo Observa\u00E7\u00F5es \u00E9 de preenchimento obrigat\u00F3rio.");
			}*/

			if (this.meta.getObservacoesCampo() == null) {
				this.meta.setObservacoesCampo(new ArrayList<ObservacaoCampo>());
			}

			ObservacaoCampo obsCampo = new ObservacaoCampo();
			obsCampo.setCampo(this.tipoCampoObservacao);
			obsCampo.setTipo(TipoObservacao.G);
			obsCampo.setDataHora(new Date());
			obsCampo.setMeta(this.meta);
			obsCampo.setDescricao(this.textoObservacaoCampo);
			obsCampo.setRemetente(appBean.getUsuarioAutenticado());

			this.meta.getObservacoesCampo().add(obsCampo);
			FacesUtil
					.addInfoMessage("Observa\u00E7\u00E3o adicionada com sucesso!");
		}/* catch (ValidateException ve) {
			FacesUtil.addErrorMessage("frmObservacao", ve.getMsgErrors());
		} */
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public boolean isPodeInserirMarcoCriticoCicloAtual() {
		if (meta != null) {
			CicloConfiguracao cc = meta.getCicloConfiguracao();
			if (cc != null) {
				List<ConfiguracaoMarcoCritico> configuracoesMarcoCritico = configuracaoMarcoCriticoService
						.findByTipoUnidadeCicloConfiguracao(
								this.meta.getTipo(), this.meta.getUnidade(),
								this.meta.getCicloConfiguracao());

				if(configuracoesMarcoCritico != null && !configuracoesMarcoCritico.isEmpty()){
					return configuracoesMarcoCritico.get(0).getDisponibilizadoInsercaoMetas();
				}
			}
		}
		return false;
	}

	public String enviarObservacaoColaborador() {
		try {
			if (StringUtils.isBlank(this.getTextoObservacao())) {
				FacesUtil.addErrorMessage("form_validacao_meta:txObservacao",
						"O campo Observa\u00E7\u00F5es \u00E9 de preenchimento obrigat\u00F3rio.");
				throw new ValidateException("O campo Observa\u00E7\u00F5es \u00E9 de preenchimento obrigat\u00F3rio.");
			}
			this.meta.setCicloConfiguracao(appBean.getCicloConfiguracaoDesempenho());
			metaService.enviarObservacaoColaboradorMeta(this.meta, getTextoObservacao());
			gestaoMetasBean.resetCampos();
			gestorGestaoMetasDesempenhoNomeBean.removerMeta(this.meta);
			pendenciasBean.removerMeta(this.meta);
			FacesUtil.addInfoMessage("Observa\u00E7\u00E3o enviada com sucesso!");
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String enviarObservacaoComite() {
		try {
			if (StringUtils.isBlank(this.getTextoObservacao())) {
				FacesUtil.addErrorMessage("form_validacao_meta:txObservacao",
						"O campo Observa\u00E7\u00F5es \u00E9 de preenchimento obrigat\u00F3rio.");
				throw new ValidateException("O campo Observa\u00E7\u00F5es \u00E9 de preenchimento obrigat\u00F3rio.");
			}
			this.meta.setCicloConfiguracao(appBean.getCicloConfiguracaoDesempenho());
			metaService.gerenteEnviarObservacaoComiteMeta(this.meta, getTextoObservacao());
			gestaoMetasBean.resetCampos();
			gestorGestaoMetasDesempenhoNomeBean.removerMeta(this.meta);
			pendenciasBean.removerMeta(this.meta);
			FacesUtil.addInfoMessage("Observa\u00E7\u00E3o enviada com sucesso!");
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String cancelarMeta() {
		try {
			if (this.meta.getStatusByFase(Fase.R) != null) {
				this.meta.getMetaStatusByFase(Fase.R).setStatus(StatusMeta.CS);
			} else if (this.meta.getStatusByFase(Fase.P) != null) {
				this.meta.getMetaStatusByFase(Fase.P).setStatus(StatusMeta.CS);
			}
			this.meta.setStatusAtual(StatusMeta.CS);
			this.metaService.save(meta);
			gestaoMetasBean.resetCampos();
			gestorGestaoMetasDesempenhoNomeBean.removerMeta(this.meta);
			pendenciasBean.removerMeta(this.meta);
			FacesUtil.addInfoMessage("Meta cancelada com sucesso.");
			return appBean.back();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String aprovarMeta() {
		try {
			this.metaService.aprovarMeta(meta, meta.getCicloConfiguracao().getTipoConfiguracao(), TipoGrupo.C);
			gestaoMetasBean.resetCampos();
			gestorGestaoMetasDesempenhoNomeBean.removerMeta(this.meta);
			pendenciasBean.removerMeta(this.meta);
			FacesUtil.addInfoMessage("Meta aprovada com sucesso.");
			return appBean.back();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public boolean verificarStatusByFase() {
		if (appBean.getCicloSelecionado().getStatusRepactuacao(TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C) == StatusCiclo.I
			|| appBean.getCicloSelecionado().getStatusPactuacao(TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C) == StatusCiclo.I) {
			return true;
		} else {
			return false;
		}
	}

	public void prepararEnvioObservacaoCampo() {
		this.textoObservacaoCampo = "";
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public List<Object[]> getRegistrosLog() {
		return registrosLog;
	}

	public void setRegistrosLog(List<Object[]> registrosLog) {
		this.registrosLog = registrosLog;
	}

	public TipoCampoObservacao getTipoCampoObservacao() {
		return tipoCampoObservacao;
	}

	public void setTipoCampoObservacao(TipoCampoObservacao tipoCampoObservacao) {
		this.tipoCampoObservacao = tipoCampoObservacao;
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
			indicadores = indicadorService.findAll();
		}
		return indicadores;
	}

	public String getTextoObservacao() {
		return textoObservacao;
	}

	public void setTextoObservacao(String textoObservacao) {
		this.textoObservacao = textoObservacao;
	}

	public String getTextoObservacaoCampo() {
		return textoObservacaoCampo;
	}

	public void setTextoObservacaoCampo(String textoObservacaoCampo) {
		this.textoObservacaoCampo = textoObservacaoCampo;
	}

}
