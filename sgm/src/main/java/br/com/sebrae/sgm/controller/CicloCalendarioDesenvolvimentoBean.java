package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import br.com.sebrae.sgm.utils.FacesUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@ConversationScoped
@Named
public class CicloCalendarioDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloCalendarioDesenvolvimentoBean.class);

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

	private Prorrogacao prorrogacaoGerente = new Prorrogacao();

	private Prorrogacao prorrogacaoColaborador = new Prorrogacao();

	private Fase faseProrrogacaoGerente = Fase.P;

	private Fase faseProrrogacaoColaborador = Fase.P;

	private List<Unidade> unidades;

	@Override
	public void init() {
		super.init();
		if (this.cicloConfiguracao.getCalendarios() == null) {
			this.cicloConfiguracao.setCalendarios(new ArrayList<Calendario>());
		}
		this.calendarioGerente = null;
		this.calendarioColaborador = null;
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
			}
		}
		return calendarioColaborador;
	}

	public void setCalendarioColaborador(Calendario calendarioColaborador) {
		this.calendarioColaborador = calendarioColaborador;
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
			getCalendarioGerente().setDtInicioAjustes(null);
		}

		if (getCalendarioGerente().getDtInicioAjustes() == null) {
			getCalendarioGerente().setDtFimAjustes(null);
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
				&& getCalendarioGerente().getDtInicioAjustes() != null) {
			if (getCalendarioGerente().getDtInicioAjustes().compareTo(getCalendarioGerente().getDtInicioPactuacao()) < 0) {
				getCalendarioGerente().setDtInicioAjustes(null);
				getCalendarioGerente().setDtFimAjustes(null);
				FacesUtil
						.addErrorMessage("form-calendario-gerente:ajustesInicio",
								"A data in\u00EDcio de ajustes deve ser maior ou igual a data in\u00EDcio de pactua\u00E7\u00E3o.");
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

		if (getCalendarioGerente().getDtFimValidacao() != null
				&& getCalendarioGerente().getDtFimMonitoramento() != null) {
			if (getCalendarioGerente().getDtFimValidacao().compareTo(getCalendarioGerente().getDtFimMonitoramento()) > 0) {
				getCalendarioGerente().setDtFimValidacao(null);
				FacesUtil.addErrorMessage("form-calendario-gerente:validacaoFinal",
						"A data final de valida\u00E7\u00E3o deve ser menor ou igual a data final de monitoramento.");
			}
		}
	}

	public void copiarDatasGerenteParaColaborador() {
		getCalendarioColaborador().setDtInicioPactuacao(getCalendarioGerente().getDtInicioPactuacao());
		getCalendarioColaborador().setDtFimPactuacao(getCalendarioGerente().getDtFimPactuacao());
		getCalendarioColaborador().setDtInicioRepactuacao(getCalendarioGerente().getDtInicioRepactuacao());
		getCalendarioColaborador().setDtFimRepactuacao(getCalendarioGerente().getDtFimRepactuacao());
		getCalendarioColaborador().setDtInicioAjustes(getCalendarioGerente().getDtInicioAjustes());
		getCalendarioColaborador().setDtFimAjustes(getCalendarioGerente().getDtFimAjustes());
		getCalendarioColaborador().setDtInicioValidacao(getCalendarioGerente().getDtInicioValidacao());
		getCalendarioColaborador().setDtFimValidacao(getCalendarioGerente().getDtFimValidacao());

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
		if (this.getCalendarioGerente() != null && this.getCalendarioGerente().getProrrogacoes() != null) {
			retorno = Lists.newArrayList(Collections2.filter(this.getCalendarioGerente().getProrrogacoes(),
					new Predicate<Prorrogacao>() {
						@Override
						public boolean apply(Prorrogacao obj) {
							return obj.getFase() == faseProrrogacaoGerente;
						}
					}));
		}
		return retorno;
	}

	public void editarProrrogacaoGerente(Prorrogacao p) {
		this.prorrogacaoGerente = p;
	}

	public void excluirProrrogacaoGerente() {
		try {
			this.calendarioGerente.getProrrogacoes().remove(this.prorrogacaoGerente);
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
			getCalendarioColaborador().setDtInicioAjustes(null);
		}

		if (getCalendarioColaborador().getDtInicioAjustes() == null) {
			getCalendarioColaborador().setDtFimAjustes(null);
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

		if (getCalendarioColaborador().getDtFimPactuacao() != null
				&& getCalendarioColaborador().getDtInicioRepactuacao() != null) {
			if (getCalendarioColaborador().getDtInicioRepactuacao().compareTo(
					getCalendarioColaborador().getDtFimPactuacao()) <= 0) {
				getCalendarioColaborador().setDtInicioRepactuacao(null);
				getCalendarioColaborador().setDtFimRepactuacao(null);
				FacesUtil
						.addErrorMessage("form-calendario-colaborador:repactuacaoInicio",
								"A data in\u00EDcio da repactua\u00E7\u00E3o deve ser maior que a data fim da pactua\u00E7\u00E3o.");
			}
		}

		if (getCalendarioColaborador().getDtInicioPactuacao() != null
				&& getCalendarioColaborador().getDtInicioAjustes() != null) {
			if (getCalendarioColaborador().getDtInicioAjustes().compareTo(
					getCalendarioColaborador().getDtInicioPactuacao()) < 0) {
				getCalendarioColaborador().setDtInicioAjustes(null);
				getCalendarioColaborador().setDtFimAjustes(null);
				FacesUtil
						.addErrorMessage("form-calendario-colaborador:ajustesInicio",
								"A data in\u00EDcio de ajustes deve ser maior ou igual a data in\u00EDcio de pactua\u00E7\u00E3o.");
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

		if (getCalendarioColaborador().getDtFimValidacao() != null
				&& getCalendarioColaborador().getDtFimMonitoramento() != null) {
			if (getCalendarioColaborador().getDtFimValidacao().compareTo(
					getCalendarioColaborador().getDtFimMonitoramento()) > 0) {
				getCalendarioColaborador().setDtFimValidacao(null);
				FacesUtil.addErrorMessage("form-calendario-colaborador:validacaoFinal",
						"A data final de valida\u00E7\u00E3o deve ser menor ou igual a data final de monitoramento.");
			}
		}
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
		if (this.getCalendarioColaborador() != null && this.getCalendarioColaborador().getProrrogacoes() != null) {
			retorno = Lists.newArrayList(Collections2.filter(this.getCalendarioColaborador().getProrrogacoes(),
					new Predicate<Prorrogacao>() {
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
			this.calendarioColaborador.getProrrogacoes().remove(this.prorrogacaoColaborador);
			this.setProrrogacaoColaborador(new Prorrogacao());
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

			getCalendarioGerente().setCicloConfiguracao(this.cicloConfiguracao);
			this.cicloConfiguracao.getCalendarios().add(getCalendarioGerente());

			if (getCalendarioColaborador().isCompleto() && getCalendarioGerente().isCompleto()) {
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
		return Arrays.asList(new Fase[] { Fase.P, Fase.R, Fase.J, Fase.V });
	}

	public List<Unidade> completeUnidade(String query) {
		List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUF(query, this.cicloConfiguracao.getCiclo()
				.getUf());
		return retorno;
	}
}
