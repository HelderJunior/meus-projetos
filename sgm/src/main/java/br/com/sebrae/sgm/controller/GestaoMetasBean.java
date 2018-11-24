package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.dto.GerenciarMetaDTO;
import br.com.sebrae.sgm.controller.dto.MetaSelecaoDTO;
import br.com.sebrae.sgm.controller.enums.AcaoComboGestao;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class GestaoMetasBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(GestaoMetasBean.class);

	@Inject
	private MetaService metaService;
	
	@Inject
	private AppBean appBean;

	@Inject
	private CicloService cicloService;

	@Inject
	private UnidadeService unidadeService;

	private boolean habilitarGridResultado;

	private List<Ciclo> ciclos = new ArrayList<Ciclo>();

	private List<Unidade> unidades;

	private Ciclo ciclo;

	private Unidade unidade;

	private Fase faseCiclo;

	private String tipoVisualizacao;

	private String textoBuscaRapidaColaborador;

	private String textoBuscaRapidaPendencias;

	private AcaoComboGestao acao;
	private boolean enviarObservacao = false;

	private DataTable dataTable;
	private List<GerenciarMetaDTO> metasGerenciar = new ArrayList<GerenciarMetaDTO>();

	private Map<Integer, Boolean> selecaoTodosMetasAprovadas = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosMetasGravadas = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosMetasCanceladas = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosObservacaoComite = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosObservacaoSuperior = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosPendenteAprovacao = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosPendenteCancelamento = new HashMap<Integer, Boolean>();

	// Tipo Pendencia
	private List<MetaSelecaoDTO> metasAprovadas = new ArrayList<MetaSelecaoDTO>();
	private DataTable dataTableMetasAprovadas;
	private Map<Integer, Boolean> selecaoTodosMetasAprovadasTipoPendencia = new HashMap<Integer, Boolean>();

	private List<MetaSelecaoDTO> metasGravadas = new ArrayList<MetaSelecaoDTO>();
	private DataTable dataTableMetasGravadas;
	private Map<Integer, Boolean> selecaoTodosMetasGravadasTipoPendencia = new HashMap<Integer, Boolean>();

	private List<MetaSelecaoDTO> metasPendenteAprovacao = new ArrayList<MetaSelecaoDTO>();
	private DataTable dataTableMetasPendenteAprovacao;
	private Map<Integer, Boolean> selecaoTodosMetasPendenteAprovacaoTipoPendencia = new HashMap<Integer, Boolean>();

	private List<MetaSelecaoDTO> metasCanceladas = new ArrayList<MetaSelecaoDTO>();
	private DataTable dataTableMetasCanceladas;
	private Map<Integer, Boolean> selecaoTodosMetasCanceladasTipoPendencia = new HashMap<Integer, Boolean>();

	private List<MetaSelecaoDTO> metasObservacaoSuperior = new ArrayList<MetaSelecaoDTO>();
	private DataTable dataTableMetasObservacaoSuperior;
	private Map<Integer, Boolean> selecaoTodosObservacaoSuperiorTipoPendencia = new HashMap<Integer, Boolean>();

	private List<MetaSelecaoDTO> metasObservacaoComite = new ArrayList<MetaSelecaoDTO>();
	private DataTable dataTableMetasObservacaoComite;
	private Map<Integer, Boolean> selecaoTodosObservacaoComiteTipoPendencia = new HashMap<Integer, Boolean>();

	private String textoObservacao;

	private void habilitarGridResultado() {
		setHabilitarGridResultado(false);
		if (tipoVisualizacao != null && (ciclo != null || unidade != null || faseCiclo != null)) {
			RequestContext.getCurrentInstance().update("form_listar_metas");
			setHabilitarGridResultado(true);
		}

	}

	// public void filtrarDataTable() {
	// if ("NC".equals(tipoVisualizacao)) {
	// buscarMetasNomeColaborador();
	// } else if ("TP".equals(tipoVisualizacao)) {
	// buscarMetasTipoPendencia();
	// }
	// RequestContext.getCurrentInstance().update("form_listar_metas");
	// RequestContext.getCurrentInstance().update("form_pesquisar");
	// }

	public void pesquisar() {
		habilitarGridResultado();
		if (tipoVisualizacao == null) {
			RequestContext.getCurrentInstance().update("form_listar_metas");
			FacesUtil.addErrorMessage("O campo Tipo de Visualiza\u00E7\u00E3o e de preenchimento obrigat\u00F3rio.");
		} else {
			if (ciclo == null && unidade == null && faseCiclo == null) {
				RequestContext.getCurrentInstance().update("form_listar_metas");
				FacesUtil.addErrorMessage("Selecione um ciclo, ou fase de ciclo ou unidade.");
			} else {
				limparSelecaoTodos();
				resetCampos();
				try {
					if ("NC".equals(tipoVisualizacao)) {
						buscarMetasNomeColaborador();
					} else if ("TP".equals(tipoVisualizacao)) {
						buscarMetasTipoPendencia();
					}
					RequestContext.getCurrentInstance().update("form_listar_metas");
					RequestContext.getCurrentInstance().update("form_pesquisar");
				} catch (Exception e) {
					FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
					log.error(e.getMessage(), e);
				}
			}

		}
	}

	private void buscarMetasNomeColaborador() {
		this.metasGerenciar = metaService.buscarMetasGerenciarDesempenho(ciclo, unidade,
				appBean.getUsuarioAutenticado(), this.faseCiclo);
	}

	private void buscarMetasTipoPendencia() {
		this.metasAprovadas = this.metaService.findMetasByParameters(this.ciclo, this.unidade, this.textoBuscaRapidaPendencias, this.faseCiclo, StatusMeta.AS, appBean.getUsuarioAutenticado());
		this.metasGravadas = this.metaService.findMetasByParameters(this.ciclo, this.unidade,
				this.textoBuscaRapidaPendencias, this.faseCiclo, StatusMeta.SA, appBean.getUsuarioAutenticado());
		this.metasPendenteAprovacao = this.metaService.findMetasByParameters(this.ciclo, this.unidade, this.textoBuscaRapidaPendencias, this.faseCiclo, StatusMeta.PA, appBean.getUsuarioAutenticado());
		this.metasCanceladas = this.metaService.findMetasByParameters(this.ciclo, this.unidade,
				this.textoBuscaRapidaPendencias, this.faseCiclo, StatusMeta.CS, appBean.getUsuarioAutenticado());
		this.metasObservacaoSuperior = this.metaService.findMetasByParameters(this.ciclo, this.unidade,
				this.textoBuscaRapidaPendencias, this.faseCiclo, StatusMeta.OS, appBean.getUsuarioAutenticado());
		this.metasObservacaoComite = this.metaService.findMetasByParameters(this.ciclo, this.unidade,
				this.textoBuscaRapidaPendencias, this.faseCiclo, StatusMeta.OC, appBean.getUsuarioAutenticado());
	}

	public List<GerenciarMetaDTO> getMetasGerenciar() {
		return metasGerenciar;
	}

	public void setMetasGerenciar(List<GerenciarMetaDTO> metasGerenciar) {
		this.metasGerenciar = metasGerenciar;
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

	public Ciclo getCiclo() {
		return ciclo;
	}

	public void setCiclo(Ciclo ciclo) {
		this.ciclo = ciclo;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public List<Fase> getFasesCiclo() {
		return Arrays.asList(new Fase[] { Fase.P, Fase.R, Fase.M, Fase.V, Fase.A });
	}

	public Fase getFaseCiclo() {
		return faseCiclo;
	}

	public void setFaseCiclo(Fase faseCiclo) {
		this.faseCiclo = faseCiclo;
	}

	public String getTipoVisualizacao() {
		return tipoVisualizacao;
	}

	public void setTipoVisualizacao(String tipoVisualizacao) {
		this.tipoVisualizacao = tipoVisualizacao;
	}

	public String getTextoBuscaRapida() {
		return textoBuscaRapidaPendencias;
	}

	public void setTextoBuscaRapida(String textoBuscaRapida) {
		this.textoBuscaRapidaPendencias = textoBuscaRapida;
	}

	public AcaoComboGestao getAcao() {
		return acao;
	}

	public void setAcao(AcaoComboGestao acao) {
		this.acao = acao;
	}

	public List<AcaoComboGestao> getAcoesComboGestao() {
		List<AcaoComboGestao> retorno = new ArrayList<AcaoComboGestao>();
		if (isPossuiTodosPendenteAprovacao() || isPossuiAlgunPendenteAprovacao()
				|| isPossuiTodosMetasPendenteAprovacaoTipoPendencia() || isPossuiAlgunPendenteAprovacaoTipoPendencia()) {
			retorno.add(AcaoComboGestao.AM);
			if (this.faseCiclo != Fase.P) {
				retorno.add(AcaoComboGestao.CM);
			} else {
				retorno.add(AcaoComboGestao.EM);
			}
		}
		retorno.add(AcaoComboGestao.EO);
		return retorno;
	}

	private DataTable getDataTable() {
		if (dataTable == null) {
			dataTable = (DataTable) FacesUtil.getFacesContext().getViewRoot()
					.findComponent("form_listar_metas:tbl_listar_metas");
		}
		return dataTable;
	}

	public Boolean getSelecaoTodosMetasAprovadas() {
		if (selecaoTodosMetasAprovadas.containsKey(getDataTable().getPage())) {
			return selecaoTodosMetasAprovadas.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosMetasAprovadas(Boolean val) {
		selecaoTodosMetasAprovadas.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosAprovados() {
		try {
			if (getSelecaoTodosMetasAprovadas()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoAprovadas(Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoAprovadas(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosMetasGravadas() {
		if (selecaoTodosMetasGravadas.containsKey(getDataTable().getPage())) {
			return selecaoTodosMetasGravadas.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosMetasGravadas(Boolean val) {
		selecaoTodosMetasGravadas.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosGravadas() {
		try {
			if (getSelecaoTodosMetasGravadas()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoGravadas(Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoGravadas(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosMetasCanceladas() {
		if (selecaoTodosMetasCanceladas.containsKey(getDataTable().getPage())) {
			return selecaoTodosMetasCanceladas.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosMetasCanceladas(Boolean val) {
		selecaoTodosMetasCanceladas.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosCancelados() {
		try {
			if (getSelecaoTodosMetasCanceladas()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoCanceladas(Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoCanceladas(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosObservacaoComite() {
		if (selecaoTodosObservacaoComite.containsKey(getDataTable().getPage())) {
			return selecaoTodosObservacaoComite.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosObservacaoComite(Boolean val) {
		this.selecaoTodosObservacaoComite.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosObservacaoComite() {
		try {
			if (getSelecaoTodosObservacaoComite()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoObservacaoComite(Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoObservacaoComite(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosObservacaoSuperior() {
		if (selecaoTodosObservacaoSuperior.containsKey(getDataTable().getPage())) {
			return selecaoTodosObservacaoSuperior.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosObservacaoSuperior(Boolean val) {
		this.selecaoTodosObservacaoSuperior.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosObservacaoSuperior() {
		try {
			if (getSelecaoTodosObservacaoSuperior()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoObservacaoSuperior(Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoObservacaoSuperior(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosPendenteAprovacao() {
		if (selecaoTodosPendenteAprovacao.containsKey(getDataTable().getPage())) {
			return selecaoTodosPendenteAprovacao.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosPendenteAprovacao(Boolean val) {
		this.selecaoTodosPendenteAprovacao.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosPendenteAprovacao() {
		try {
			if (getSelecaoTodosPendenteAprovacao()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoPendenteAprovacao(Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoPendenteAprovacao(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosPendenteCancelamento() {
		if (selecaoTodosPendenteCancelamento.containsKey(getDataTable().getPage())) {
			return selecaoTodosPendenteCancelamento.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosPendenteCancelamento(Boolean val) {
		this.selecaoTodosPendenteCancelamento.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosPendenteCancelamento() {
		try {
			if (getSelecaoTodosPendenteCancelamento()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoPendenteCancelamento(Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoPendenteCancelamento(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public boolean isPossuiItemSelecionado() {
		if (isPossuiTodosAprovados() || isPossuiTodosGravadas() || isPossuiTodosCancelados()
				|| isPossuiTodosObservacaoComite() || isPossuiTodosObservacaoSuperior()
				|| isPossuiTodosPendenteAprovacao() || isPossuiTodosPendenteCancelamento()) {
			return true;
		}

		for (GerenciarMetaDTO dto : this.metasGerenciar) {
			if (dto.getSelecaoAprovadas() || dto.getSelecaoGravadas() || dto.getSelecaoCanceladas()
					|| dto.getSelecaoObservacaoComite() || dto.getSelecaoObservacaoSuperior()
					|| dto.getSelecaoPendenteAprovacao() || dto.getSelecaoPendenteCancelamento()) {
				return true;
			}
		}

		// visualizacao por tipo de pendencia
		if (isPossuiTodosMetasAprovadasTipoPendencia() || isPossuiTodosMetasCanceladasTipoPendencia()
				|| isPossuiTodosMetasObservacaoSuperiorTipoPendencia()
				|| isPossuiTodosMetasObservacaoComiteTipoPendencia() || isPossuiTodosMetasGravadasTipoPendencia()
				|| isPossuiTodosMetasPendenteAprovacaoTipoPendencia()) {
			return true;
		}

		for (MetaSelecaoDTO m : this.metasAprovadas) {
			if (m.getSelecionado()) {
				return true;
			}
		}

		for (MetaSelecaoDTO m : this.metasGravadas) {
			if (m.getSelecionado()) {
				return true;
			}
		}

		for (MetaSelecaoDTO m : this.metasPendenteAprovacao) {
			if (m.getSelecionado()) {
				return true;
			}
		}

		for (MetaSelecaoDTO m : this.metasCanceladas) {
			if (m.getSelecionado()) {
				return true;
			}
		}
		for (MetaSelecaoDTO m : this.metasObservacaoSuperior) {
			if (m.getSelecionado()) {
				return true;
			}
		}
		for (MetaSelecaoDTO m : this.metasObservacaoComite) {
			if (m.getSelecionado()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosAprovados() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosMetasAprovadas.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosGravadas() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosMetasGravadas.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosCancelados() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosMetasCanceladas.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosObservacaoComite() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosObservacaoComite.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosObservacaoSuperior() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosObservacaoSuperior.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosPendenteAprovacao() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosPendenteAprovacao.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private boolean isPossuiAlgunPendenteAprovacao() {
		if (this.metasGerenciar != null) {
			for (GerenciarMetaDTO gmd : this.metasGerenciar) {
				if (gmd.getSelecaoPendenteAprovacao()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPossuiAlgunPendenteAprovacaoTipoPendencia() {
		if (this.metasPendenteAprovacao != null) {
			for (MetaSelecaoDTO ms : this.metasPendenteAprovacao) {
				if (ms.getSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}

	private Boolean isPossuiTodosPendenteCancelamento() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosPendenteCancelamento.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosMetasAprovadasTipoPendencia() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosMetasAprovadasTipoPendencia.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosMetasGravadasTipoPendencia() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosMetasGravadasTipoPendencia.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosMetasPendenteAprovacaoTipoPendencia() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosMetasPendenteAprovacaoTipoPendencia.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosMetasCanceladasTipoPendencia() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosMetasCanceladasTipoPendencia.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosMetasObservacaoSuperiorTipoPendencia() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosObservacaoSuperiorTipoPendencia.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosMetasObservacaoComiteTipoPendencia() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosObservacaoComiteTipoPendencia.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private DataTable getDataTableMetasAprovadas() {
		if (dataTableMetasAprovadas == null) {
			dataTableMetasAprovadas = (DataTable) FacesUtil.getFacesContext().getViewRoot()
					.findComponent("form_listar_metas:tbl_metas_aprovadas");
		}
		return dataTableMetasAprovadas;
	}

	private DataTable getDataTableMetasGravadas() {
		if (dataTableMetasGravadas == null) {
			dataTableMetasGravadas = (DataTable) FacesUtil.getFacesContext().getViewRoot()
					.findComponent("form_listar_metas:tbl_metas_gravadas");
		}
		return dataTableMetasGravadas;
	}

	private DataTable getDataTableMetasPendenteAprovacao() {
		if (dataTableMetasPendenteAprovacao == null) {
			dataTableMetasPendenteAprovacao = (DataTable) FacesUtil.getFacesContext().getViewRoot()
					.findComponent("form_listar_metas:tbl_metas_pendentes_aprovacao");
		}
		return dataTableMetasPendenteAprovacao;
	}

	private DataTable getDataTableMetasCanceladas() {
		if (dataTableMetasCanceladas == null) {
			dataTableMetasCanceladas = (DataTable) FacesUtil.getFacesContext().getViewRoot()
					.findComponent("form_listar_metas:tbl_metas_canceladas");
		}
		return dataTableMetasCanceladas;
	}

	public List<MetaSelecaoDTO> getMetasAprovadas() {
		return metasAprovadas;
	}

	public void setMetasAprovadas(List<MetaSelecaoDTO> metasAprovadas) {
		this.metasAprovadas = metasAprovadas;
	}

	public List<MetaSelecaoDTO> getMetasGravadas() {
		return metasGravadas;
	}

	public void setMetasGravadas(List<MetaSelecaoDTO> metasGravadas) {
		this.metasGravadas = metasGravadas;
	}

	public List<MetaSelecaoDTO> getMetasPendenteAprovacao() {
		return metasPendenteAprovacao;
	}

	public void setMetasPendenteAprovacao(List<MetaSelecaoDTO> metasPendenteAprovacao) {
		this.metasPendenteAprovacao = metasPendenteAprovacao;
	}

	public List<MetaSelecaoDTO> getMetasCanceladas() {
		return metasCanceladas;
	}

	public void setMetasCanceladas(List<MetaSelecaoDTO> metasCanceladas) {
		this.metasCanceladas = metasCanceladas;
	}

	public String getTextoObservacao() {
		return textoObservacao;
	}

	public void setTextoObservacao(String textoObservacao) {
		this.textoObservacao = textoObservacao;
	}

	public boolean isEnviarObservacao() {
		return enviarObservacao;
	}

	public void setEnviarObservacao(boolean enviarObservacao) {
		this.enviarObservacao = enviarObservacao;
	}

	public Boolean getSelecaoTodosMetasAprovadasTipoPendencia() {
		if (selecaoTodosMetasAprovadasTipoPendencia.containsKey(getDataTableMetasAprovadas().getPage())) {
			return selecaoTodosMetasAprovadasTipoPendencia.get(getDataTableMetasAprovadas().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosMetasAprovadasTipoPendencia(Boolean val) {
		selecaoTodosMetasAprovadasTipoPendencia.put(getDataTableMetasAprovadas().getPage(), val);
	}

	public void alterouSelecaoTodosAprovadosTipoPendencia() {
		try {
			if (getSelecaoTodosMetasAprovadasTipoPendencia()) {
				int min = getDataTableMetasAprovadas().getPage() * getDataTableMetasAprovadas().getRows();
				int max = (getDataTableMetasAprovadas().getPage() * getDataTableMetasAprovadas().getRows())
						+ getDataTableMetasAprovadas().getRows();

				if (max > metasAprovadas.size()) {
					max = metasAprovadas.size();
				}

				for (int i = min; i < max; i++) {
					metasAprovadas.get(i).setSelecionado(Boolean.TRUE);
				}
			} else {
				int min = getDataTableMetasAprovadas().getPage() * getDataTableMetasAprovadas().getRows();
				int max = (getDataTableMetasAprovadas().getPage() * getDataTableMetasAprovadas().getRows())
						+ getDataTableMetasAprovadas().getRows();

				if (max > metasAprovadas.size()) {
					max = metasAprovadas.size();
				}

				for (int i = min; i < max; i++) {
					metasAprovadas.get(i).setSelecionado(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosMetasGravadasTipoPendencia() {
		if (selecaoTodosMetasGravadasTipoPendencia.containsKey(getDataTableMetasGravadas().getPage())) {
			return selecaoTodosMetasGravadasTipoPendencia.get(getDataTableMetasGravadas().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosMetasGravadasTipoPendencia(Boolean val) {
		selecaoTodosMetasGravadasTipoPendencia.put(getDataTableMetasGravadas().getPage(), val);
	}

	public void alterouSelecaoTodosGravadasTipoPendencia() {
		try {
			if (getSelecaoTodosMetasGravadasTipoPendencia()) {
				int min = getDataTableMetasGravadas().getPage() * getDataTableMetasGravadas().getRows();
				int max = (getDataTableMetasGravadas().getPage() * getDataTableMetasGravadas().getRows())
						+ getDataTableMetasGravadas().getRows();

				if (max > metasGravadas.size()) {
					max = metasGravadas.size();
				}

				for (int i = min; i < max; i++) {
					metasGravadas.get(i).setSelecionado(Boolean.TRUE);
				}
			} else {
				int min = getDataTableMetasGravadas().getPage() * getDataTableMetasGravadas().getRows();
				int max = (getDataTableMetasGravadas().getPage() * getDataTableMetasGravadas().getRows())
						+ getDataTableMetasGravadas().getRows();

				if (max > metasGravadas.size()) {
					max = metasGravadas.size();
				}

				for (int i = min; i < max; i++) {
					metasGravadas.get(i).setSelecionado(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosMetasPendenteAprovacaoTipoPendencia() {
		if (selecaoTodosMetasPendenteAprovacaoTipoPendencia.containsKey(getDataTableMetasPendenteAprovacao().getPage())) {
			return selecaoTodosMetasPendenteAprovacaoTipoPendencia.get(getDataTableMetasPendenteAprovacao().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosMetasPendenteAprovacaoTipoPendencia(Boolean val) {
		selecaoTodosMetasPendenteAprovacaoTipoPendencia.put(getDataTableMetasGravadas().getPage(), val);
	}

	public void alterouSelecaoTodosPendenteAprovacaoTipoPendencia() {
		try {
			if (getSelecaoTodosMetasPendenteAprovacaoTipoPendencia()) {
				int min = getDataTableMetasPendenteAprovacao().getPage()
						* getDataTableMetasPendenteAprovacao().getRows();
				int max = (getDataTableMetasPendenteAprovacao().getPage() * getDataTableMetasPendenteAprovacao()
						.getRows()) + getDataTableMetasPendenteAprovacao().getRows();

				if (max > metasPendenteAprovacao.size()) {
					max = metasPendenteAprovacao.size();
				}

				for (int i = min; i < max; i++) {
					metasPendenteAprovacao.get(i).setSelecionado(Boolean.TRUE);
				}
			} else {
				int min = getDataTableMetasPendenteAprovacao().getPage()
						* getDataTableMetasPendenteAprovacao().getRows();
				int max = (getDataTableMetasPendenteAprovacao().getPage() * getDataTableMetasPendenteAprovacao()
						.getRows()) + getDataTableMetasPendenteAprovacao().getRows();

				if (max > metasPendenteAprovacao.size()) {
					max = metasPendenteAprovacao.size();
				}

				for (int i = min; i < max; i++) {
					metasPendenteAprovacao.get(i).setSelecionado(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosMetasCanceladasTipoPendencia() {
		if (selecaoTodosMetasCanceladasTipoPendencia.containsKey(getDataTableMetasCanceladas().getPage())) {
			return selecaoTodosMetasAprovadasTipoPendencia.get(getDataTableMetasCanceladas().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosMetasCanceladasTipoPendencia(Boolean val) {
		selecaoTodosMetasCanceladasTipoPendencia.put(getDataTableMetasCanceladas().getPage(), val);
	}

	public void alterouSelecaoTodosCanceladasTipoPendencia() {
		try {
			if (getSelecaoTodosMetasCanceladasTipoPendencia()) {
				int min = getDataTableMetasCanceladas().getPage() * getDataTableMetasCanceladas().getRows();

				int max = (getDataTableMetasCanceladas().getPage() * getDataTableMetasCanceladas().getRows())
						+ getDataTableMetasCanceladas().getRows();

				if (max > metasCanceladas.size()) {
					max = metasCanceladas.size();
				}

				for (int i = min; i < max; i++) {
					metasCanceladas.get(i).setSelecionado(Boolean.TRUE);
				}
			} else {
				int min = getDataTableMetasCanceladas().getPage() * getDataTableMetasCanceladas().getRows();
				int max = (getDataTableMetasCanceladas().getPage() * getDataTableMetasCanceladas().getRows())
						+ getDataTableMetasCanceladas().getRows();

				if (max > metasCanceladas.size()) {
					max = metasCanceladas.size();
				}

				for (int i = min; i < max; i++) {
					metasCanceladas.get(i).setSelecionado(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosObservacaoComiteTipoPendencia() {
		if (selecaoTodosObservacaoComiteTipoPendencia.containsKey(getDataTableMetasObservacaoComite().getPage())) {
			return selecaoTodosObservacaoComiteTipoPendencia.get(getDataTableMetasObservacaoComite().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosObservacaoComiteTipoPendencia(Boolean val) {
		selecaoTodosObservacaoComiteTipoPendencia.put(getDataTableMetasObservacaoComite().getPage(), val);
	}

	public void alterouSelecaoTodosObservacaoComiteTipoPendencia() {
		try {
			if (getSelecaoTodosMetasCanceladasTipoPendencia()) {
				int min = getDataTableMetasObservacaoComite().getPage() * getDataTableMetasObservacaoComite().getRows();

				int max = (getDataTableMetasObservacaoComite().getPage() * getDataTableMetasObservacaoComite()
						.getRows()) + getDataTableMetasObservacaoComite().getRows();

				if (max > metasObservacaoComite.size()) {
					max = metasObservacaoComite.size();
				}

				for (int i = min; i < max; i++) {
					metasObservacaoComite.get(i).setSelecionado(Boolean.TRUE);
				}
			} else {
				int min = getDataTableMetasObservacaoComite().getPage() * getDataTableMetasObservacaoComite().getRows();
				int max = (getDataTableMetasObservacaoComite().getPage() * getDataTableMetasObservacaoComite()
						.getRows()) + getDataTableMetasObservacaoComite().getRows();

				if (max > metasObservacaoComite.size()) {
					max = metasObservacaoComite.size();
				}

				for (int i = min; i < max; i++) {
					metasObservacaoComite.get(i).setSelecionado(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<MetaSelecaoDTO> getMetasObservacaoComite() {
		return metasObservacaoComite;
	}

	public void setMetasObservacaoComite(List<MetaSelecaoDTO> metasObservacaoComite) {
		this.metasObservacaoComite = metasObservacaoComite;
	}

	private DataTable getDataTableMetasObservacaoComite() {
		if (dataTableMetasObservacaoComite == null) {
			dataTableMetasObservacaoComite = (DataTable) FacesUtil.getFacesContext().getViewRoot()
					.findComponent("form_listar_metas:tbl_metas_observacao_comite");
		}
		return dataTableMetasObservacaoComite;
	}

	public Boolean getSelecaoTodosObservacaoSuperiorTipoPendencia() {
		if (selecaoTodosObservacaoSuperiorTipoPendencia.containsKey(getDataTableMetasObservacaoSuperior().getPage())) {
			return selecaoTodosObservacaoSuperiorTipoPendencia.get(getDataTableMetasObservacaoSuperior().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosObservacaoSuperiorTipoPendencia(Boolean val) {
		selecaoTodosObservacaoSuperiorTipoPendencia.put(getDataTableMetasObservacaoSuperior().getPage(), val);
	}

	public void alterouSelecaoTodosObservacaoSuperiorTipoPendencia() {
		try {
			if (getSelecaoTodosMetasCanceladasTipoPendencia()) {
				int min = getDataTableMetasObservacaoSuperior().getPage()
						* getDataTableMetasObservacaoSuperior().getRows();

				int max = (getDataTableMetasObservacaoSuperior().getPage() * getDataTableMetasObservacaoSuperior()
						.getRows()) + getDataTableMetasObservacaoSuperior().getRows();

				if (max > metasObservacaoSuperior.size()) {
					max = metasObservacaoSuperior.size();
				}

				for (int i = min; i < max; i++) {
					metasObservacaoSuperior.get(i).setSelecionado(Boolean.TRUE);
				}
			} else {
				int min = getDataTableMetasObservacaoSuperior().getPage()
						* getDataTableMetasObservacaoSuperior().getRows();
				int max = (getDataTableMetasObservacaoSuperior().getPage() * getDataTableMetasObservacaoSuperior()
						.getRows()) + getDataTableMetasObservacaoSuperior().getRows();

				if (max > metasObservacaoSuperior.size()) {
					max = metasObservacaoSuperior.size();
				}

				for (int i = min; i < max; i++) {
					metasObservacaoSuperior.get(i).setSelecionado(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<MetaSelecaoDTO> getMetasObservacaoSuperior() {
		return metasObservacaoSuperior;
	}

	public void setMetasObservacaoSuperior(List<MetaSelecaoDTO> metasObservacaoSuperior) {
		this.metasObservacaoSuperior = metasObservacaoSuperior;
	}

	public DataTable getDataTableMetasObservacaoSuperior() {
		if (dataTableMetasObservacaoSuperior == null) {
			dataTableMetasObservacaoSuperior = (DataTable) FacesUtil.getFacesContext().getViewRoot()
					.findComponent("form_listar_metas:tbl_metas_observacao_superior");
		}
		return dataTableMetasObservacaoSuperior;
	}

	public void resetCamposVisualizacaoColaborador() {
		this.metasGerenciar = new ArrayList<GerenciarMetaDTO>();
		limparSelecaoVisualizacaoColaborador();
	}

	public void resetCampos() {
		metasGerenciar = new ArrayList<GerenciarMetaDTO>();
		metasAprovadas = new ArrayList<MetaSelecaoDTO>();
		metasGravadas = new ArrayList<MetaSelecaoDTO>();
		metasPendenteAprovacao = new ArrayList<MetaSelecaoDTO>();
		metasCanceladas = new ArrayList<MetaSelecaoDTO>();
		metasObservacaoSuperior = new ArrayList<MetaSelecaoDTO>();
		metasObservacaoComite = new ArrayList<MetaSelecaoDTO>();
		this.acao = null;
		limparSelecaoTodos();
	}

	public void limparSelecaoVisualizacaoColaborador() {
		selecaoTodosMetasAprovadas = new HashMap<Integer, Boolean>();
		selecaoTodosMetasCanceladas = new HashMap<Integer, Boolean>();
		selecaoTodosObservacaoComite = new HashMap<Integer, Boolean>();
		selecaoTodosObservacaoSuperior = new HashMap<Integer, Boolean>();
		selecaoTodosPendenteAprovacao = new HashMap<Integer, Boolean>();
		selecaoTodosPendenteCancelamento = new HashMap<Integer, Boolean>();
	}

	private void limparSelecaoTodos() {
		limparSelecaoVisualizacaoColaborador();
		selecaoTodosMetasAprovadasTipoPendencia = new HashMap<Integer, Boolean>();
		selecaoTodosMetasGravadasTipoPendencia = new HashMap<Integer, Boolean>();
		selecaoTodosMetasCanceladasTipoPendencia = new HashMap<Integer, Boolean>();
		selecaoTodosObservacaoSuperiorTipoPendencia = new HashMap<Integer, Boolean>();
		selecaoTodosObservacaoComiteTipoPendencia = new HashMap<Integer, Boolean>();
	}

	public void aprovarMetasSelecionadas() {
		if (tipoVisualizacao.equals("NC")) {
			aprovarMetasNomeColaborador();
		} else if (tipoVisualizacao.equals("TP")) {
			aprovarMetasTipoPendencia();
		}
		pesquisar();
		this.acao = null;
		limparSelecaoTodos();
	}

	public void cancelarMetasSelecionadas() {
		if (tipoVisualizacao.equals("NC")) {
			cancelarMetasNomeColaborador();
		} else if (tipoVisualizacao.equals("TP")) {
			cancelarMetasTipoPendencia();
		}
		pesquisar();
		this.acao = null;
		limparSelecaoTodos();
	}

	public void excluirMetasSelecionadas() {
		if (tipoVisualizacao.equals("NC")) {
			excluirMetasNomeColaborador();
		} else if (tipoVisualizacao.equals("TP")) {
			excluirMetasTipoPendencia();
		}
		pesquisar();
		this.acao = null;
		limparSelecaoTodos();
	}

	private void cancelarMetasNomeColaborador() {
		try {
			int qtd = 0;
			for (GerenciarMetaDTO mdto : this.metasGerenciar) {
				try {
					if (mdto.getSelecaoAprovadas()) {
						for (Meta m : mdto.getMetasAprovadas()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoCanceladas()) {
						for (Meta m : mdto.getMetasCanceladas()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteAprovacao()) {
						for (Meta m : mdto.getMetasPendenteAprovacao()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteCancelamento()) {
						for (Meta m : mdto.getMetasPendenteCancelamento()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoSuperior()) {
						for (Meta m : mdto.getMetasObservacaoSuperior()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoComite()) {
						for (Meta m : mdto.getMetasObservacaoComite()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}
			resetCampos();
			FacesUtil.addInfoMessage(qtd + " metas canceladas com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void excluirMetasNomeColaborador() {
		try {
			int qtd = 0;
			for (GerenciarMetaDTO mdto : this.metasGerenciar) {
				Meta meta = null;
				try {
					if (mdto.getSelecaoAprovadas()) {
						for (Meta m : mdto.getMetasAprovadas()) {
							meta = m;
							metaService.delete(m);
							qtd++;
						}
					}

					if (mdto.getSelecaoCanceladas()) {
						for (Meta m : mdto.getMetasCanceladas()) {
							meta = m;
							metaService.delete(m);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteAprovacao()) {
						for (Meta m : mdto.getMetasPendenteAprovacao()) {
							meta = m;
							metaService.delete(m);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteCancelamento()) {
						for (Meta m : mdto.getMetasPendenteCancelamento()) {
							meta = m;
							metaService.delete(m);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoSuperior()) {
						for (Meta m : mdto.getMetasObservacaoSuperior()) {
							meta = m;
							metaService.delete(m);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoComite()) {
						for (Meta m : mdto.getMetasObservacaoComite()) {
							meta = m;
							metaService.delete(m);
							qtd++;
						}
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				} catch (Exception e) {
					FacesUtil
							.addErrorMessage("Meta"
									+ meta.getCodigo()
									+ " n\u00E3o pode ser exclu\u00EDda, provavelmente est\u00E1 sendo utilizada por outro registro no sistema");
				}
			}
			resetCampos();
			FacesUtil.addInfoMessage(qtd + " metas excluidas com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void aprovarMetasNomeColaborador() {
		try {
			int qtd = 0;
			for (GerenciarMetaDTO mdto : this.metasGerenciar) {
				try {
					if (mdto.getSelecaoAprovadas()) {
						for (Meta m : mdto.getMetasAprovadas()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoCanceladas()) {
						for (Meta m : mdto.getMetasCanceladas()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteAprovacao()) {
						for (Meta m : mdto.getMetasPendenteAprovacao()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteCancelamento()) {
						for (Meta m : mdto.getMetasPendenteCancelamento()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoSuperior()) {
						for (Meta m : mdto.getMetasObservacaoSuperior()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoComite()) {
						for (Meta m : mdto.getMetasObservacaoComite()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}

			resetCampos();
			FacesUtil.addInfoMessage(qtd + " metas aprovadas com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararEnviarObservacaoSelecionadas() {
		try {
			setEnviarObservacao(true);
			setTextoObservacao("");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void enviarObservacaoMetasSelecionadas() {
		if (tipoVisualizacao.equals("NC")) {
			enviarObservacaoNomeColaborador();
		} else if (tipoVisualizacao.equals("TP")) {
			enviarObservacaoMetasTipoPendencia();
		}
		pesquisar();
		this.acao = null;
		limparSelecaoTodos();
	}

	private void enviarObservacaoNomeColaborador() {
		try {
			int qtd = 0;
			for (GerenciarMetaDTO mdto : this.metasGerenciar) {
				try {
					if (mdto.getSelecaoAprovadas()) {
						for (Meta m : mdto.getMetasAprovadas()) {
							metaService.enviarObservacaoSuperiorMeta(m, getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoCanceladas()) {
						for (Meta m : mdto.getMetasCanceladas()) {
							metaService.enviarObservacaoSuperiorMeta(m, getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteAprovacao()) {
						for (Meta m : mdto.getMetasPendenteAprovacao()) {
							metaService.enviarObservacaoSuperiorMeta(m, getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteCancelamento()) {
						for (Meta m : mdto.getMetasPendenteCancelamento()) {
							metaService.enviarObservacaoSuperiorMeta(m, getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoSuperior()) {
						for (Meta m : mdto.getMetasObservacaoSuperior()) {
							metaService.enviarObservacaoSuperiorMeta(m, getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoComite()) {
						for (Meta m : mdto.getMetasObservacaoComite()) {
							metaService.enviarObservacaoSuperiorMeta(m, getTextoObservacao());
							qtd++;
						}
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}
			FacesUtil.addInfoMessage(qtd + " metas envidas observa\u00E7\u00E3o com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void aprovarMetasTipoPendencia() {
		try {
			if (this.metasPendenteAprovacao != null) {
				int qtd = 0;
				for (MetaSelecaoDTO msdto : this.metasPendenteAprovacao) {
					try {
						if (msdto.getSelecionado()) {
							if (msdto.getMeta().getTipo() == TipoMeta.I)
								metaService.aprovarMeta(msdto.getMeta(), TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (msdto.getMeta().getTipo() == TipoMeta.E)
								metaService.aprovarMeta(msdto.getMeta(), TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					} catch (ValidateException e) {
						FacesUtil.addErrorMessage(e.getMsgErrors());
					}
				}
				FacesUtil.addInfoMessage(qtd + " metas envidas aprovadas com sucesso!");
			} else {
				FacesUtil.addInfoMessage("Nenhuma meta a ser aprovada.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void cancelarMetasTipoPendencia() {
		try {
			if (this.metasPendenteAprovacao != null && !this.metasPendenteAprovacao.isEmpty()) {
				int qtd = 0;
				for (MetaSelecaoDTO msdto : this.metasPendenteAprovacao) {
					try {
						if (msdto.getSelecionado()) {
							if (msdto.getMeta().getTipo() == TipoMeta.I)
								metaService.cancelarMeta(msdto.getMeta(), TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
							if (msdto.getMeta().getTipo() == TipoMeta.E)
								metaService.cancelarMeta(msdto.getMeta(), TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
							qtd++;
						}
					} catch (ValidateException e) {
						FacesUtil.addErrorMessage(e.getMsgErrors());
					}
				}
				FacesUtil.addInfoMessage(qtd + " metas envidas canceladas com sucesso!");
			} else {
				FacesUtil.addInfoMessage("Nenhuma meta a ser cancelada.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void excluirMetasTipoPendencia() {
		try {
			if (this.metasPendenteAprovacao != null && !this.metasPendenteAprovacao.isEmpty()) {
				int qtd = 0;
				for (MetaSelecaoDTO msdto : this.metasPendenteAprovacao) {
					try {
						if (msdto.getSelecionado()) {
							metaService.delete(msdto.getMeta());
							qtd++;
						}
					} catch (ValidateException e) {
						FacesUtil.addErrorMessage(e.getMsgErrors());
					} catch (Exception e) {
						FacesUtil
								.addErrorMessage("Meta"
										+ msdto.getMeta().getCodigo()
										+ " n\u00E3o pode ser exclu\u00EDda, provavelmente est\u00E1 sendo utilizada por outro registro no sistema");
					}
				}
				FacesUtil.addInfoMessage(qtd + " metas envidas canceladas com sucesso!");
			} else {
				FacesUtil.addInfoMessage("Nenhuma meta a ser cancelada.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void enviarObservacaoMetasTipoPendencia() {
		try {
			int qtd = 0;

			for (MetaSelecaoDTO mdto : this.metasAprovadas) {
				try {
					if (mdto.getSelecionado()) {
						metaService.enviarObservacaoSuperiorMeta(mdto.getMeta(), getTextoObservacao());
						qtd++;
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}

			for (MetaSelecaoDTO mdto : this.metasGravadas) {
				try {
					if (mdto.getSelecionado()) {
						metaService.enviarObservacaoSuperiorMeta(mdto.getMeta(), getTextoObservacao());
						qtd++;
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}

			for (MetaSelecaoDTO mdto : this.metasPendenteAprovacao) {
				try {
					if (mdto.getSelecionado()) {
						metaService.enviarObservacaoSuperiorMeta(mdto.getMeta(), getTextoObservacao());
						qtd++;
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}

			for (MetaSelecaoDTO mdto : this.metasCanceladas) {
				try {
					if (mdto.getSelecionado()) {
						metaService.enviarObservacaoSuperiorMeta(mdto.getMeta(), getTextoObservacao());
						qtd++;
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}

			for (MetaSelecaoDTO mdto : this.metasObservacaoComite) {
				try {
					if (mdto.getSelecionado()) {
						metaService.enviarObservacaoSuperiorMeta(mdto.getMeta(), getTextoObservacao());
						qtd++;
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}

			for (MetaSelecaoDTO mdto : this.metasObservacaoSuperior) {
				try {
					if (mdto.getSelecionado()) {
						metaService.enviarObservacaoSuperiorMeta(mdto.getMeta(), getTextoObservacao());
						qtd++;
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}

			FacesUtil.addInfoMessage(qtd + " metas envidas observa\u00E7\u00E3o com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<Unidade> completeUnidade(String descricao) {
		List<Unidade> retorno = unidadeService.findByDescricao(descricao);
		return retorno;
	}

	public boolean isHabilitarGridResultado() {
		return habilitarGridResultado;
	}

	public void setHabilitarGridResultado(boolean habilitarGridResultado) {
		this.habilitarGridResultado = habilitarGridResultado;
	}

	public String getTextoBuscaRapidaColaborador() {
		return textoBuscaRapidaColaborador;
	}

	public void setTextoBuscaRapidaColaborador(String textoBuscaRapidaColaborador) {
		this.textoBuscaRapidaColaborador = textoBuscaRapidaColaborador;
	}

	public String getTextoBuscaRapidaPendencias() {
		return textoBuscaRapidaPendencias;
	}

	public void setTextoBuscaRapidaPendencias(String textoBuscaRapidaPendencias) {
		this.textoBuscaRapidaPendencias = textoBuscaRapidaPendencias;
	}
}
