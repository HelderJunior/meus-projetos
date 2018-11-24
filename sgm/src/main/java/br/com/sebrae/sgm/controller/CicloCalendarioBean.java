package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Calendario;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.Prorrogacao;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.DateUtils;
import br.com.sebrae.sgm.utils.FacesUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@ConversationScoped
@Named
public class CicloCalendarioBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloCalendarioBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private UsuarioService usuarioService;
	
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private Boolean visualizando = Boolean.FALSE;

	private Calendario calendarioGerente;

	private Calendario calendarioColaborador;

	private Calendario calendarioEquipe;

	private Prorrogacao prorrogacaoGerente = new Prorrogacao();

	private Prorrogacao prorrogacaoColaborador = new Prorrogacao();

	private Prorrogacao prorrogacaoEquipe = new Prorrogacao();

	private Fase faseProrrogacaoGerente = Fase.P;

	private Fase faseProrrogacaoColaborador = Fase.P;

	private Fase faseProrrogacaoEquipe = Fase.P;

	private List<Unidade> unidades;

	private boolean dtInicioAuditoria = true;

	private boolean dtFimAuditoria = true;


	public boolean isDtInicioAuditoria() {
		return dtInicioAuditoria;
	}

	public void setDtInicioAuditoria(boolean dtInicioAuditoria) {
		this.dtInicioAuditoria = dtInicioAuditoria;
	}

	@Override
	public void init() {
		super.init();
		if (this.cicloConfiguracao.getCalendarios() == null) {
			this.cicloConfiguracao.setCalendarios(new ArrayList<Calendario>());
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

	public Calendario getCalendarioGerente() {
		if (calendarioGerente == null) {
			calendarioGerente = this.cicloConfiguracao.getCalendarioGerente();
			if (calendarioGerente == null) {
				calendarioGerente = new Calendario();
				calendarioGerente.setTipo(TipoGrupo.G);
				calendarioGerente.setCicloConfiguracao(this.cicloConfiguracao);
			}
		}
		return calendarioGerente;
	}

	public void setCalendarioGerente(Calendario calendarioGerente) {
		this.calendarioGerente = calendarioGerente;
	}

	public Calendario getCalendarioColaborador() {
		if (calendarioColaborador == null) {
			calendarioColaborador = this.cicloConfiguracao.getCalendarioColaborador();
			if (calendarioColaborador == null) {
				calendarioColaborador = new Calendario();
				calendarioColaborador.setTipo(TipoGrupo.C);
				calendarioColaborador.setCicloConfiguracao(this.cicloConfiguracao);
			}
		}
		return calendarioColaborador;
	}

	public void setCalendarioColaborador(Calendario calendarioColaborador) {
		this.calendarioColaborador = calendarioColaborador;
	}

	public Calendario getCalendarioEquipe() {
		if (calendarioEquipe == null) {
			calendarioEquipe = this.cicloConfiguracao.getCalendarioEquipe();
			if (calendarioEquipe == null) {
				calendarioEquipe = new Calendario();
				calendarioEquipe.setTipo(TipoGrupo.E);
				calendarioEquipe.setCicloConfiguracao(this.cicloConfiguracao);
			}
		}
		return calendarioEquipe;
	}

	public void setCalendarioEquipe(Calendario calendarioEquipe) {
		this.calendarioEquipe = calendarioEquipe;
	}

	public Prorrogacao getProrrogacaoGerente() {
		return prorrogacaoGerente;
	}

	public void setProrrogacaoGerente(Prorrogacao prorrogacaoGerente) {
		this.prorrogacaoGerente = prorrogacaoGerente;
	}

	public Prorrogacao getProrrogacaoColaborador() {
		return prorrogacaoColaborador;
	}

	public void setProrrogacaoColaborador(Prorrogacao prorrogacaoColaborador) {
		this.prorrogacaoColaborador = prorrogacaoColaborador;
	}

	public Prorrogacao getProrrogacaoEquipe() {
		return prorrogacaoEquipe;
	}

	public void setProrrogacaoEquipe(Prorrogacao prorrogacaoEquipe) {
		this.prorrogacaoEquipe = prorrogacaoEquipe;
	}

	public Fase getFaseProrrogacaoGerente() {
		return faseProrrogacaoGerente;
	}

	public void setFaseProrrogacaoGerente(Fase faseProrrogacaoGerente) {
		this.faseProrrogacaoGerente = faseProrrogacaoGerente;
	}

	public Fase getFaseProrrogacaoColaborador() {
		return faseProrrogacaoColaborador;
	}

	public void setFaseProrrogacaoColaborador(Fase faseProrrogacaoColaborador) {
		this.faseProrrogacaoColaborador = faseProrrogacaoColaborador;
	}

	public Fase getFaseProrrogacaoEquipe() {
		return faseProrrogacaoEquipe;
	}

	public void setFaseProrrogacaoEquipe(Fase faseProrrogacaoEquipe) {
		this.faseProrrogacaoEquipe = faseProrrogacaoEquipe;
	}

	public List<Unidade> getUnidades() {
		if (unidades == null || unidades.isEmpty()) {
			unidades = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
		}
		return unidades;
	}

	public List<Usuario> getUsuariosGerente() {
		List<Usuario> retorno = new ArrayList<Usuario>();
		if (prorrogacaoGerente.getUnidade() != null) {
			retorno = usuarioService.findByCodUnidadeMesmaDiretoria(prorrogacaoGerente.getUnidade().getUnidadePK()
					.getCodigo());
		} else {
			retorno = new ArrayList<Usuario>();
		}

		return retorno;
	}

	public void validarDatasGerente() {

		if (getCalendarioGerente().getDtInicioPactuacao() == null) {
			getCalendarioGerente().setDtFimPactuacao(null);
			getCalendarioGerente().setDtInicioRepactuacao(null);
		}

		if (getCalendarioGerente().getDtInicioRepactuacao() == null) {
			getCalendarioGerente().setDtFimRepactuacao(null);
			getCalendarioGerente().setDtInicioMonitoramento(null);
		}

		if (getCalendarioGerente().getDtInicioMonitoramento() == null) {
			getCalendarioGerente().setDtFimMonitoramento(null);
			getCalendarioGerente().setDtInicioValidacao(null);
		}

		if (getCalendarioGerente().getDtInicioValidacao() == null) {
			getCalendarioGerente().setDtFimValidacao(null);
		}

		if (getCalendarioGerente().getDtInicioPactuacao() != null && getCalendarioGerente().getDtFimPactuacao() != null) {
			if (getCalendarioGerente().getDtFimPactuacao().compareTo(getCalendarioGerente().getDtInicioPactuacao()) < 0) {
				getCalendarioGerente().setDtFimPactuacao(null);
				FacesUtil.addErrorMessage("form-calendario-gerente:pactuacaoFinal",
						"A data fim da pactua\u00E7\u00E3o deve ser maior ou igual a data de in\u00EDcio.");
			}
		}

		if (getCalendarioGerente().getDtFimPactuacao() != null
				&& getCalendarioGerente().getDtInicioRepactuacao() != null) {
			if (getCalendarioGerente().getDtInicioRepactuacao().compareTo(getCalendarioGerente().getDtFimPactuacao()) <= 0) {
				getCalendarioGerente().setDtInicioRepactuacao(null);
				getCalendarioGerente().setDtFimRepactuacao(null);
				FacesUtil
						.addErrorMessage("form-calendario-gerente:repactuacaoInicio",
								"A data in\u00EDcio da repactua\u00E7\u00E3o deve ser maior que a data fim da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioGerente().getDtInicioPactuacao() != null
				&& getCalendarioGerente().getDtInicioMonitoramento() != null) {
			if (getCalendarioGerente().getDtInicioMonitoramento().compareTo(
					getCalendarioGerente().getDtInicioPactuacao()) < 0) {
				getCalendarioGerente().setDtInicioMonitoramento(null);
				getCalendarioGerente().setDtFimMonitoramento(null);
				FacesUtil
						.addErrorMessage("form-calendario-gerente:monitoramentoInicio",
								"A data in\u00EDcio do monitoramento deve ser maior ou igual a data in\u00EDcio da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioGerente().getDtInicioPactuacao() != null
				&& getCalendarioGerente().getDtInicioValidacao() != null) {
			if (getCalendarioGerente().getDtInicioValidacao().compareTo(getCalendarioGerente().getDtInicioPactuacao()) < 0) {
				getCalendarioGerente().setDtInicioValidacao(null);
				getCalendarioGerente().setDtFimValidacao(null);
				FacesUtil
						.addErrorMessage("form-calendario-gerente:validacaoInicio",
								"A data in\u00EDcio da valida\u00E7\u00E3o deve ser maior ou igual a data in\u00EDcio da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioGerente().getDtFimValidacao() != null) {
			dtInicioAuditoria = false;
		} else {
			dtInicioAuditoria = true;
		}

		if (getCalendarioGerente().getDtInicioAuditoria() != null) {
			dtFimAuditoria = false;

		} else {
			dtFimAuditoria = true;
		}

		if (getCalendarioGerente().getDtInicioAuditoria() != null
				&& getCalendarioGerente().getDtFimValidacao() != null) {
			if (getCalendarioGerente().getDtInicioAuditoria().compareTo(getCalendarioGerente().getDtFimValidacao()) <= 0) {

				getCalendarioGerente().setDtInicioAuditoria(null);
				getCalendarioGerente().setDtFimAuditoria(null);
				dtFimAuditoria = true;
				FacesUtil.addErrorMessage("form-calendario-gerente:auditoriaInicio",
						"A data in\u00EDcio da auditoria deve ser maior que a data fim da valida\u00E7\u00E3o.");
			}
		}

		if (getCalendarioGerente().getDtInicioAuditoria() != null && getCalendarioGerente().getDtFimAuditoria() != null) {

			Long fimAuditoria = DateUtils.toMilisegundos(getCalendarioGerente().getDtFimAuditoria());
			Long inicioAuditoria = DateUtils.toMilisegundos(getCalendarioGerente().getDtInicioAuditoria());

			if (!(fimAuditoria >= inicioAuditoria)) {
				// getCalendarioGerente().setDtInicioAuditoria(null);
				getCalendarioGerente().setDtFimAuditoria(null);
				FacesUtil.addErrorMessage("form-calendario-gerente:auditoriaFinal",
						"A data fim da auditoria deve ser maior ou igual a data in\u00EDcio.");
			}
		}

		/*
		 * if (getCalendarioGerente().getDtFimValidacao() != null && getCalendarioGerente().getDtFimMonitoramento() !=
		 * null) { if (getCalendarioGerente ().getDtFimValidacao().compareTo(getCalendarioGerente
		 * ().getDtFimMonitoramento()) > 0) { getCalendarioGerente().setDtFimValidacao(null);
		 * FacesUtil.addErrorMessage("form-calendario-gerente:validacaoFinal",
		 * "A data final de valida\u00E7\u00E3o deve ser menor ou igual a data final de monitoramento." ); } }
		 */
	}

	public void copiarDatasGerenteParaColaborador() {
		getCalendarioColaborador().setDtInicioPactuacao(getCalendarioGerente().getDtInicioPactuacao());
		getCalendarioColaborador().setDtFimPactuacao(getCalendarioGerente().getDtFimPactuacao());
		getCalendarioColaborador().setDtInicioRepactuacao(getCalendarioGerente().getDtInicioRepactuacao());
		getCalendarioColaborador().setDtFimRepactuacao(getCalendarioGerente().getDtFimRepactuacao());
		getCalendarioColaborador().setDtInicioMonitoramento(getCalendarioGerente().getDtInicioMonitoramento());
		getCalendarioColaborador().setDtFimMonitoramento(getCalendarioGerente().getDtFimMonitoramento());
		getCalendarioColaborador().setDtInicioValidacao(getCalendarioGerente().getDtInicioValidacao());
		getCalendarioColaborador().setDtFimValidacao(getCalendarioGerente().getDtFimValidacao());

	}

	public void copiarDatasColaboradorParaEquipe() {
		getCalendarioEquipe().setDtInicioPactuacao(getCalendarioColaborador().getDtInicioPactuacao());
		getCalendarioEquipe().setDtFimPactuacao(getCalendarioColaborador().getDtFimPactuacao());
		getCalendarioEquipe().setDtInicioRepactuacao(getCalendarioColaborador().getDtInicioRepactuacao());
		getCalendarioEquipe().setDtFimRepactuacao(getCalendarioColaborador().getDtFimRepactuacao());
		getCalendarioEquipe().setDtInicioMonitoramento(getCalendarioColaborador().getDtInicioMonitoramento());
		getCalendarioEquipe().setDtFimMonitoramento(getCalendarioColaborador().getDtFimMonitoramento());
		getCalendarioEquipe().setDtInicioValidacao(getCalendarioColaborador().getDtInicioValidacao());
		getCalendarioEquipe().setDtFimValidacao(getCalendarioColaborador().getDtFimValidacao());
	}

	public void alterouDataInicioProrrogacaoGerente() {
		if (getProrrogacaoGerente().getDtInicio() == null) {
			getProrrogacaoGerente().setDtFim(null);
		}
		if (getProrrogacaoGerente().getDtInicio() != null && getProrrogacaoGerente().getDtFim() != null) {
			if (getProrrogacaoGerente().getDtFim().compareTo(getProrrogacaoGerente().getDtInicio()) <= 0) {
				FacesUtil.addErrorMessage("form-prorrogacao-gerente:terminoProrrogacaoGerente",
						"A data final da prorroga\u00E7\u00E3o deve ser maior que a data inicial.");
			}
		}
	}

	public void adicionarProrrogacaoGerente() {
		try {
			if (this.calendarioGerente.getProrrogacoes() == null) {
				this.calendarioGerente.setProrrogacoes(new ArrayList<Prorrogacao>());
			}

			this.getProrrogacaoGerente().setFase(faseProrrogacaoGerente);
			this.getProrrogacaoGerente().setCalendario(calendarioGerente);
			if (!this.calendarioGerente.getProrrogacoes().contains(getProrrogacaoGerente())) {
				this.calendarioGerente.getProrrogacoes().add(getProrrogacaoGerente());
			}

			if (!this.cicloConfiguracao.getCalendarios().contains(this.calendarioGerente)) {
				this.cicloConfiguracao.getCalendarios().add(getCalendarioGerente());
			}

			getCalendarioGerente().setCicloConfiguracao(cicloConfiguracao);
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);

			setProrrogacaoGerente(new Prorrogacao());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<Prorrogacao> getProrrogacoesGerente() {
		List<Prorrogacao> retorno = new ArrayList<Prorrogacao>();
		if (this.cicloConfiguracao.getCalendarioGerente() != null) {
			List<Prorrogacao> prorrogacoesGerente = cicloConfiguracaoService.loadCalendarioAndProrrogacao(cicloConfiguracao.getCiclo(), 
					cicloConfiguracao.getCalendarioGerente().getTipo());
			
			retorno = Lists.newArrayList(Collections2.filter(prorrogacoesGerente, new Predicate<Prorrogacao>() {
				@Override
				public boolean apply(Prorrogacao obj) {
					return obj.getFase() == faseProrrogacaoGerente;
					
				}
			}));
		}
		/*if (this.getCalendarioGerente() != null && this.getCalendarioGerente().getProrrogacoes() != null) {
			retorno = Lists.newArrayList(Collections2.filter(this.getCalendarioGerente().getProrrogacoes(),
					new Predicate<Prorrogacao>() {
						@Override
						public boolean apply(Prorrogacao obj) {
							return obj.getFase() == faseProrrogacaoGerente;
						}
					}));
		}*/
		return retorno;
	}

	public void editarProrrogacaoGerente(Prorrogacao p) {
		this.prorrogacaoGerente = p;
	}

	public void excluirProrrogacaoGerente() {
		try {
			List<Prorrogacao> prorrogacoesGerente = cicloConfiguracaoService.loadCalendarioAndProrrogacao(cicloConfiguracao.getCiclo(), 
					cicloConfiguracao.getCalendarioGerente().getTipo());
			
			prorrogacoesGerente.remove(this.prorrogacaoGerente);
			cicloConfiguracaoService.delete(this.prorrogacaoGerente);
			getProrrogacoesGerente();
			//this.calendarioGerente.getProrrogacoes().remove(this.prorrogacaoGerente);
			this.setProrrogacaoGerente(new Prorrogacao());
			
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void validarDatasColaborador() {

		if (getCalendarioColaborador().getDtInicioPactuacao() == null) {
			getCalendarioColaborador().setDtFimPactuacao(null);
			getCalendarioColaborador().setDtInicioRepactuacao(null);
		}

		if (getCalendarioColaborador().getDtInicioRepactuacao() == null) {
			getCalendarioColaborador().setDtFimRepactuacao(null);
			getCalendarioColaborador().setDtInicioMonitoramento(null);
		}

		if (getCalendarioColaborador().getDtInicioMonitoramento() == null) {
			getCalendarioColaborador().setDtFimMonitoramento(null);
			getCalendarioColaborador().setDtInicioValidacao(null);
		}

		if (getCalendarioColaborador().getDtInicioValidacao() == null) {
			getCalendarioColaborador().setDtFimValidacao(null);
		}

		if (getCalendarioColaborador().getDtInicioPactuacao() != null
				&& getCalendarioColaborador().getDtFimPactuacao() != null) {
			if (getCalendarioColaborador().getDtFimPactuacao().compareTo(
					getCalendarioColaborador().getDtInicioPactuacao()) < 0) {
				getCalendarioColaborador().setDtFimPactuacao(null);
				FacesUtil.addErrorMessage("form-calendario-colaborador:pactuacaoFinal",
						"A data fim da pactua\u00E7\u00E3o deve ser maior ou igual a data de in\u00EDcio.");
			}
		}

		if (getCalendarioColaborador().getDtFimPactuacao() != null && getCalendarioColaborador().getDtInicioRepactuacao() != null) {
			if (getCalendarioColaborador().getDtInicioRepactuacao().compareTo(getCalendarioColaborador().getDtFimPactuacao()) <= 0) {
				
				getCalendarioColaborador().setDtInicioRepactuacao(null);
				getCalendarioColaborador().setDtFimRepactuacao(null);
				FacesUtil
						.addErrorMessage("form-calendario-colaborador:repactuacaoInicio",
								"A data in\u00EDcio da repactua\u00E7\u00E3o deve ser maior que a data final da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioColaborador().getDtInicioPactuacao() != null
				&& getCalendarioColaborador().getDtInicioMonitoramento() != null) {
			if (getCalendarioColaborador().getDtInicioMonitoramento().compareTo(
					getCalendarioColaborador().getDtInicioPactuacao()) < 0) {
				getCalendarioColaborador().setDtInicioMonitoramento(null);
				getCalendarioColaborador().setDtFimMonitoramento(null);
				FacesUtil
						.addErrorMessage("form-calendario-colaborador:monitoramentoInicio",
								"A data in\u00EDcio do monitoramento deve ser maior ou igual a data in\u00EDcio da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioColaborador().getDtInicioPactuacao() != null
				&& getCalendarioColaborador().getDtInicioValidacao() != null) {
			if (getCalendarioColaborador().getDtInicioValidacao().compareTo(
					getCalendarioColaborador().getDtInicioPactuacao()) < 0) {
				getCalendarioColaborador().setDtInicioValidacao(null);
				getCalendarioColaborador().setDtFimValidacao(null);
				FacesUtil
						.addErrorMessage("form-calendario-colaborador:validacaoInicio",
								"A data in\u00EDcio da valida\u00E7\u00E3o deve ser maior ou igual a data in\u00EDcio da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioColaborador().getDtInicioValidacao() != null && getCalendarioColaborador().getDtInicioAuditoria() != null) {
			if (getCalendarioColaborador().getDtInicioAuditoria().compareTo(getCalendarioColaborador().getDtFimValidacao()) <= 0) {

				getCalendarioColaborador().setDtInicioAuditoria(null);
				getCalendarioColaborador().setDtFimAuditoria(null);
				FacesUtil
						.addErrorMessage("form-calendario-colaborador:auditoriaInicio",
								"A data in\u00EDcio da auditoria deve ser maior que a data fim da valida\u00E7\u00E3o.");
			}
		}

		if (getCalendarioColaborador().getDtInicioAuditoria() != null
				&& getCalendarioColaborador().getDtFimAuditoria() != null) {
			if (getCalendarioColaborador().getDtFimAuditoria().compareTo(
					getCalendarioColaborador().getDtInicioAuditoria()) < 0) {
				getCalendarioColaborador().setDtInicioAuditoria(null);
				getCalendarioColaborador().setDtFimAuditoria(null);
				FacesUtil.addErrorMessage("form-calendario-colaborador:auditoriaFinal",
						"A data fim da auditoria deve ser maior ou igual a data in\u00EDcio.");
			}
		}

		/*
		 * if (getCalendarioColaborador().getDtFimValidacao() != null &&
		 * getCalendarioColaborador().getDtFimMonitoramento() != null) { if
		 * (getCalendarioColaborador().getDtFimValidacao().compareTo(
		 * getCalendarioColaborador().getDtFimMonitoramento()) > 0) {
		 * getCalendarioColaborador().setDtFimValidacao(null); FacesUtil.addErrorMessage
		 * ("form-calendario-colaborador:validacaoFinal",
		 * "A data final de valida\u00E7\u00E3o deve ser menor ou igual a data final de monitoramento." ); } }
		 */
	}

	public List<Usuario> getUsuariosColaborador() {
		List<Usuario> retorno = new ArrayList<Usuario>();
		if (prorrogacaoColaborador.getUnidade() != null) {
			retorno = usuarioService.findByCodUnidadeMesmaDiretoria(prorrogacaoColaborador.getUnidade().getUnidadePK()
					.getCodigo());
		} else {
			retorno = new ArrayList<Usuario>();
		}
		return retorno;
	}

	public void alterouDataInicioProrrogacaoColaborador() {
		if (getProrrogacaoColaborador().getDtInicio() == null) {
			getProrrogacaoColaborador().setDtFim(null);
		}
		if (getProrrogacaoColaborador().getDtInicio() != null && getProrrogacaoColaborador().getDtFim() != null) {
			if (getProrrogacaoColaborador().getDtFim().compareTo(getProrrogacaoColaborador().getDtInicio()) <= 0) {
				FacesUtil.addErrorMessage("form-prorrogacao-colaborador:terminoProrrogacaoColaborador",
						"A data final da prorroga\u00E7\u00E3o deve ser maior que a data inicial.");
			}
		}
	}

	public void adicionarProrrogacaoColaborador() {
		try {
			if (this.calendarioColaborador.getProrrogacoes() == null) {
				this.calendarioColaborador.setProrrogacoes(new ArrayList<Prorrogacao>());
			}

			this.getProrrogacaoColaborador().setFase(faseProrrogacaoColaborador);
			this.getProrrogacaoColaborador().setCalendario(calendarioColaborador);
			if (!this.calendarioColaborador.getProrrogacoes().contains(getProrrogacaoColaborador())) {
				this.calendarioColaborador.getProrrogacoes().add(getProrrogacaoColaborador());
			}

			if (!this.cicloConfiguracao.getCalendarios().contains(this.calendarioColaborador)) {
				this.cicloConfiguracao.getCalendarios().add(getCalendarioColaborador());
			}

			getCalendarioColaborador().setCicloConfiguracao(this.cicloConfiguracao);
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);

			setProrrogacaoColaborador(new Prorrogacao());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<Prorrogacao> getProrrogacoesColaborador() {
		List<Prorrogacao> retorno = new ArrayList<Prorrogacao>();
		if (this.cicloConfiguracao.getCalendarioColaborador() != null) {
			List<Prorrogacao> prorrogacoesColaborador = cicloConfiguracaoService.loadCalendarioAndProrrogacao(this.cicloConfiguracao.getCiclo(), 
					cicloConfiguracao.getCalendarioColaborador().getTipo());
			
			retorno = Lists.newArrayList(Collections2.filter(prorrogacoesColaborador, new Predicate<Prorrogacao>() {
				@Override
				public boolean apply(Prorrogacao obj) {
					return obj.getFase() == faseProrrogacaoColaborador;
				}
			}));
		}
		return retorno;
	}

	public void editarProrrogacaoColaborador(Prorrogacao p) {
		this.prorrogacaoColaborador = p;
	}

	public void excluirProrrogacaoColaborador() {
		try {
			List<Prorrogacao> prorrogacoesColaborador = cicloConfiguracaoService.loadCalendarioAndProrrogacao(this.cicloConfiguracao.getCiclo(), 
					cicloConfiguracao.getCalendarioColaborador().getTipo());
			
			prorrogacoesColaborador.remove(this.prorrogacaoColaborador);
			cicloConfiguracaoService.delete(this.prorrogacaoColaborador);
			//this.calendarioColaborador.getProrrogacoes().remove(this.prorrogacaoColaborador);
			this.setProrrogacaoColaborador(new Prorrogacao());
			getProrrogacoesColaborador();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void validarDatasEquipe() {

		if (getCalendarioEquipe().getDtInicioPactuacao() == null) {
			getCalendarioEquipe().setDtFimPactuacao(null);
			getCalendarioEquipe().setDtInicioRepactuacao(null);
		}

		if (getCalendarioEquipe().getDtInicioRepactuacao() == null) {
			getCalendarioEquipe().setDtFimRepactuacao(null);
			getCalendarioEquipe().setDtInicioMonitoramento(null);
		}

		if (getCalendarioEquipe().getDtInicioMonitoramento() == null) {
			getCalendarioEquipe().setDtFimMonitoramento(null);
			getCalendarioEquipe().setDtInicioValidacao(null);
		}

		if (getCalendarioEquipe().getDtInicioValidacao() == null) {
			getCalendarioEquipe().setDtFimValidacao(null);
		}

		if (getCalendarioEquipe().getDtInicioPactuacao() != null && getCalendarioEquipe().getDtFimPactuacao() != null) {
			if (getCalendarioEquipe().getDtFimPactuacao().compareTo(getCalendarioEquipe().getDtInicioPactuacao()) < 0) {
				getCalendarioEquipe().setDtFimPactuacao(null);
				FacesUtil.addErrorMessage("form-calendario-equipe:pactuacaoFinal",
						"A data fim da pactua\u00E7\u00E3o deve ser maior ou igual a data de in\u00EDcio.");
			}
		}

		if (getCalendarioEquipe().getDtFimPactuacao() != null && getCalendarioEquipe().getDtInicioRepactuacao() != null) {
			if (getCalendarioEquipe().getDtInicioRepactuacao().compareTo(getCalendarioEquipe().getDtFimPactuacao()) <= 0) {
				getCalendarioEquipe().setDtInicioRepactuacao(null);
				getCalendarioEquipe().setDtFimRepactuacao(null);
				FacesUtil
						.addErrorMessage("form-calendario-equipe:repactuacaoInicio",
								"A data in\u00EDcio da repactua\u00E7\u00E3o deve ser maior que a data fim da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioEquipe().getDtInicioPactuacao() != null
				&& getCalendarioEquipe().getDtInicioMonitoramento() != null) {
			if (getCalendarioEquipe().getDtInicioMonitoramento()
					.compareTo(getCalendarioEquipe().getDtInicioPactuacao()) < 0) {
				getCalendarioEquipe().setDtInicioMonitoramento(null);
				getCalendarioEquipe().setDtFimMonitoramento(null);
				FacesUtil
						.addErrorMessage("form-calendario-equipe:monitoramentoInicio",
								"A data in\u00EDcio do monitoramento deve ser maior ou igual a data in\u00EDcio da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioEquipe().getDtInicioPactuacao() != null
				&& getCalendarioEquipe().getDtInicioValidacao() != null) {
			if (getCalendarioEquipe().getDtInicioValidacao().compareTo(getCalendarioEquipe().getDtInicioPactuacao()) < 0) {
				getCalendarioEquipe().setDtInicioValidacao(null);
				getCalendarioEquipe().setDtFimValidacao(null);
				FacesUtil
						.addErrorMessage("form-calendario-equipe:validacaoInicio",
								"A data in\u00EDcio da valida\u00E7\u00E3o deve ser maior ou igual a data in\u00EDcio da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioEquipe().getDtInicioValidacao() != null
				&& getCalendarioEquipe().getDtInicioAuditoria() != null) {
			if (getCalendarioEquipe().getDtInicioAuditoria().compareTo(getCalendarioEquipe().getDtFimValidacao()) <= 0) {
				getCalendarioEquipe().setDtInicioAuditoria(null);
				getCalendarioEquipe().setDtFimAuditoria(null);
				FacesUtil
						.addErrorMessage("form-calendario-equipe:auditoriaInicio",
								"A data in\u00EDcio da auditoria deve ser maior que a data fim da valida\u00E7\u00E3o.");
			}
		}

		if (getCalendarioEquipe().getDtInicioAuditoria() != null && getCalendarioEquipe().getDtFimAuditoria() != null) {
			if (getCalendarioEquipe().getDtFimAuditoria().compareTo(getCalendarioEquipe().getDtInicioAuditoria()) < 0) {
				getCalendarioEquipe().setDtInicioAuditoria(null);
				getCalendarioEquipe().setDtFimAuditoria(null);
				FacesUtil.addErrorMessage("form-calendario-colaborador:auditoriaFinal",
						"A data fim da auditoria deve ser maior ou igual a data in\u00EDcio.");
			}
		}

		/*
		 * if (getCalendarioEquipe().getDtFimValidacao() != null && getCalendarioEquipe().getDtFimMonitoramento() !=
		 * null) { if (getCalendarioEquipe ().getDtFimValidacao().compareTo(getCalendarioEquipe
		 * ().getDtFimMonitoramento()) > 0) { getCalendarioEquipe().setDtFimValidacao(null);
		 * FacesUtil.addErrorMessage("form-calendario-equipe:validacaoFinal",
		 * "A data final de valida\u00E7\u00E3o deve ser menor ou igual a data final de monitoramento." ); } }
		 */
	}

	public List<Usuario> getUsuariosEquipe() {
		List<Usuario> retorno = new ArrayList<Usuario>();
		if (prorrogacaoEquipe.getUnidade() != null) {
			retorno = usuarioService.findByCodUnidadeMesmaDiretoria(prorrogacaoEquipe.getUnidade().getUnidadePK().getCodigo());
		} else {
			retorno = new ArrayList<Usuario>();
		}
		return retorno;
	}

	public void alterouDataInicioProrrogacaoEquipe() {
		if (getProrrogacaoEquipe().getDtInicio() == null) {
			getProrrogacaoEquipe().setDtFim(null);
		}
		if (getProrrogacaoEquipe().getDtInicio() != null && getProrrogacaoEquipe().getDtFim() != null) {
			if (getProrrogacaoEquipe().getDtFim().compareTo(getProrrogacaoEquipe().getDtInicio()) <= 0) {
				FacesUtil.addErrorMessage("form-prorrogacao-equipe:terminoProrrogacaoEquipe",
						"A data final da prorroga\u00E7\u00E3o deve ser maior que a data inicial.");
			}
		}
	}

	public void adicionarProrrogacaoEquipe() {
		try {
			if (this.calendarioEquipe.getProrrogacoes() == null) {
				this.calendarioEquipe.setProrrogacoes(new ArrayList<Prorrogacao>());
			}

			this.getProrrogacaoEquipe().setFase(faseProrrogacaoEquipe);
			this.getProrrogacaoEquipe().setCalendario(calendarioEquipe);
			if (!this.calendarioEquipe.getProrrogacoes().contains(getProrrogacaoEquipe())) {
				this.calendarioEquipe.getProrrogacoes().add(getProrrogacaoEquipe());
			}

			if (!this.cicloConfiguracao.getCalendarios().contains(this.calendarioEquipe)) {
				this.cicloConfiguracao.getCalendarios().add(getCalendarioEquipe());
			}

			getCalendarioEquipe().setCicloConfiguracao(this.cicloConfiguracao);
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);

			setProrrogacaoEquipe(new Prorrogacao());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<Prorrogacao> getProrrogacoesEquipe() {
		List<Prorrogacao> retorno = new ArrayList<Prorrogacao>();
		if (this.cicloConfiguracao.getCalendarioEquipe() != null) {
			List<Prorrogacao> prorrogacoesEquipe = cicloConfiguracaoService.loadCalendarioAndProrrogacao(this.cicloConfiguracao.getCiclo(),
					cicloConfiguracao.getCalendarioColaborador().getTipo());
			
			retorno = Lists.newArrayList(Collections2.filter(prorrogacoesEquipe, new Predicate<Prorrogacao>() {
				@Override
				public boolean apply(Prorrogacao obj) {
					return obj.getFase() == faseProrrogacaoEquipe;
				}
			}));
		}
		return retorno;
	}

	public void editarProrrogacaoEquipe(Prorrogacao p) {
		this.prorrogacaoEquipe = p;
	}

	public void excluirProrrogacaoEquipe() {
		try {
			List<Prorrogacao> prorrogacoesEquipe = cicloConfiguracaoService.loadCalendarioAndProrrogacao(this.cicloConfiguracao.getCiclo(),
					cicloConfiguracao.getCalendarioColaborador().getTipo());
			
			prorrogacoesEquipe.remove(this.prorrogacaoEquipe);
			cicloConfiguracaoService.delete(this.prorrogacaoEquipe);
			
			//this.calendarioEquipe.getProrrogacoes().remove(this.prorrogacaoEquipe);
			this.setProrrogacaoEquipe(new Prorrogacao());
			getProrrogacoesEquipe();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public String salvar() {
		try {
			if (this.cicloConfiguracao.getCalendarios() == null) {
				this.cicloConfiguracao.setCalendarios(new ArrayList<Calendario>());
			}

			this.cicloConfiguracao.getCalendarios().clear();
			getCalendarioColaborador().setCicloConfiguracao(this.cicloConfiguracao);
			this.cicloConfiguracao.getCalendarios().add(getCalendarioColaborador());

			getCalendarioEquipe().setCicloConfiguracao(this.cicloConfiguracao);
			this.cicloConfiguracao.getCalendarios().add(getCalendarioEquipe());

			getCalendarioGerente().setCicloConfiguracao(this.cicloConfiguracao);
			this.cicloConfiguracao.getCalendarios().add(getCalendarioGerente());

			if (getCalendarioColaborador().isCompleto() && getCalendarioEquipe().isCompleto()
					&& getCalendarioGerente().isCompleto()) {
				this.cicloConfiguracao.setStatusConfiguracaoCalendario(StatusConfiguracao.C);
			}

			try {
				cicloConfiguracaoService.save(cicloConfiguracao);
				cicloService.save(cicloConfiguracao.getCiclo());
			} catch (Exception e) {
				// do nothing
			}
			
			FacesUtil.addInfoMessage("Par\u00E2metros salvos com sucesso!");
			return "/pages/ciclo/manter.xhtml";
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public List<Fase> getFases() {
		return Arrays.asList(new Fase[] { Fase.P, Fase.R, Fase.M, Fase.V, Fase.A });
	}

	public List<Unidade> completeUnidade(String query) {
		List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUF(query, this.cicloConfiguracao.getCiclo()
				.getUf());
		return retorno;
	}

	public boolean isDtFimAuditoria() {
		return dtFimAuditoria;
	}

	public void setDtFimAuditoria(boolean dtFimAuditoria) {
		this.dtFimAuditoria = dtFimAuditoria;
	}
}
