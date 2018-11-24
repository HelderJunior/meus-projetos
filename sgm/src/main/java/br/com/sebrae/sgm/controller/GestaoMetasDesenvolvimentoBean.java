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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.dto.GerenciarMetaDTO;
import br.com.sebrae.sgm.controller.enums.AcaoComboGestao;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.SolucaoEducacionalMeta;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusSolucaoEducacional;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.SolucaoEducacionalMetaService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class GestaoMetasDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory
			.getLogger(GestaoMetasDesenvolvimentoBean.class);

	@Inject
	private MetaService metaService;

	@Inject
	private CicloService cicloService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private UnidadeService unidadeService;
	
	@Inject
	private SolucaoEducacionalMetaService solucaoEducacionalMetaService;

	private List<Ciclo> ciclos = new ArrayList<Ciclo>();

	private List<Unidade> unidades;

	private Ciclo ciclo;

	private Unidade unidade;

	private Usuario usuario;

	private Fase faseCiclo;

	private String textoBuscaRapida;

	private AcaoComboGestao acao;
	private boolean enviarObservacao = false;

	private DataTable dataTable;
	private List<GerenciarMetaDTO> metasGerenciar = new ArrayList<GerenciarMetaDTO>();

	private Map<Integer, Boolean> selecaoTodosMetasAprovadas = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosSolucoesAprovadas = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosSolucoesPendentes = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosObservacaoComite = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosObservacaoSuperior = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosPendenteAprovacao = new HashMap<Integer, Boolean>();

	private Map<Integer, Boolean> selecaoTodosSolucoesRealizadas = new HashMap<Integer, Boolean>();

	private String textoObservacao;

	public void pesquisar() {
		if(ciclo == null && unidade == null && faseCiclo == null && usuario == null){
			FacesUtil.addErrorMessage("Entre com pelo menos um par\u00E2metro de filtro.");
		}
		limparSelecaoTodos();
		try {
			buscarMetasNomeColaborador();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void buscarMetasNomeColaborador() {
		String nomeUsuario = null;
		if(usuario != null){
			nomeUsuario = usuario.getNome();
		}
		this.metasGerenciar = metaService.buscarMetasGerenciarDesenvolvimento(ciclo, unidade, nomeUsuario, this.faseCiclo);
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
		return Arrays.asList(new Fase[]{Fase.P, Fase.R, Fase.J, Fase.V});
	}

	public Fase getFaseCiclo() {
		return faseCiclo;
	}

	public void setFaseCiclo(Fase faseCiclo) {
		this.faseCiclo = faseCiclo;
	}

	public String getTextoBuscaRapida() {
		return textoBuscaRapida;
	}

	public void setTextoBuscaRapida(String textoBuscaRapida) {
		this.textoBuscaRapida = textoBuscaRapida;
	}

	public AcaoComboGestao getAcao() {
		return acao;
	}

	public void setAcao(AcaoComboGestao acao) {
		this.acao = acao;
	}

	public List<AcaoComboGestao> getAcoesComboGestao() {
		List<AcaoComboGestao> retorno = new ArrayList<AcaoComboGestao>();
		boolean status = false;
		if (isPossuiTodosPendenteAprovacao()
				|| isPossuiAlgunPendenteAprovacao()) {
			retorno.add(AcaoComboGestao.AM);
			retorno.add(AcaoComboGestao.CM);
		}

		if (isPossuiTodosPendenteAprovacao()
				|| isPossuiAlgumSulacaoPendenteAprovacao()
				|| isPossuiAlgumSulacaoAprovada()) {
			
			if (this.faseCiclo != Fase.J) {
				retorno.add(AcaoComboGestao.AS);
				retorno.add(AcaoComboGestao.CS);
				retorno.add(AcaoComboGestao.EO);
			} 
			status = true;
		}
		if (!status || isPossuiAlgumSulacaoRealizada()) {
			retorno.add(AcaoComboGestao.EO);
		}
		status = false;
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
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoAprovadas(Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoAprovadas(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosSolucoesAprovadas() {
		if (selecaoTodosSolucoesAprovadas.containsKey(getDataTable().getPage())) {
			return selecaoTodosSolucoesAprovadas.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosSolucoesAprovadas(Boolean val) {
		selecaoTodosSolucoesAprovadas.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosSolucoesAprovadas() {
		try {
			if (getSelecaoTodosSolucoesAprovadas()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();
				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}
				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoSolucaoAprovadas(
							Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();
				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}
				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoSolucaoAprovadas(
							Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosSolucoesPendentes() {
		if (selecaoTodosSolucoesPendentes.containsKey(getDataTable().getPage())) {
			return selecaoTodosSolucoesPendentes.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosSolucoesPendentes(Boolean val) {
		selecaoTodosSolucoesPendentes.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosSolucoesPendentes() {
		try {
			if (getSelecaoTodosSolucoesPendentes()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoSolucaoPendenteAprovacao(
							Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoSolucaoPendenteAprovacao(
							Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
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
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoObservacaoComite(
							Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoObservacaoComite(
							Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosObservacaoSuperior() {
		if (selecaoTodosObservacaoSuperior
				.containsKey(getDataTable().getPage())) {
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
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoObservacaoSuperior(
							Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoObservacaoSuperior(
							Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
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
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoPendenteAprovacao(
							Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoPendenteAprovacao(
							Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public Boolean getSelecaoTodosSolucoesRealizadas() {
		if (selecaoTodosSolucoesRealizadas
				.containsKey(getDataTable().getPage())) {
			return selecaoTodosSolucoesRealizadas.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodosSolucoesRealizadas(Boolean val) {
		this.selecaoTodosSolucoesRealizadas.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodosSolucoesRealizadas() {
		try {
			if (getSelecaoTodosSolucoesRealizadas()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoSolucaoRealizadas(
							Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows())
						+ getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecaoSolucaoRealizadas(
							Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public boolean isPossuiItemSelecionado() {
		if (isPossuiTodosAprovados() || isPossuiTodosSolucoesAprovadas()
				|| isPossuiTodosSolucoesPendentes()
				|| isPossuiTodosObservacaoComite()
				|| isPossuiTodosObservacaoSuperior()
				|| isPossuiTodosPendenteAprovacao()
				|| isPossuiTodosSolucoesRealizadas()) {
			return true;
		}

		for (GerenciarMetaDTO dto : this.metasGerenciar) {
			if (dto.getSelecaoAprovadas() || dto.getSelecaoGravadas()
					|| dto.getSelecaoCanceladas()
					|| dto.getSelecaoObservacaoComite()
					|| dto.getSelecaoObservacaoSuperior()
					|| dto.getSelecaoPendenteAprovacao()
					|| dto.getSelecaoPendenteCancelamento()
					|| dto.getSelecaoSolucaoPendenteAprovacao()
					|| dto.getSelecaoSolucaoAprovadas()
					|| dto.getSelecaoSolucaoRealizadas()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosAprovados() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosMetasAprovadas
				.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosSolucoesAprovadas() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosSolucoesAprovadas
				.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosSolucoesPendentes() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosSolucoesPendentes
				.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosObservacaoComite() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosObservacaoComite
				.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosObservacaoSuperior() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosObservacaoSuperior
				.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
	}

	private Boolean isPossuiTodosPendenteAprovacao() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosPendenteAprovacao
				.entrySet();
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

	private boolean isPossuiAlgumSulacaoPendenteAprovacao() {
		if (this.metasGerenciar != null) {
			for (GerenciarMetaDTO gmd : this.metasGerenciar) {
				if (gmd.getSelecaoSolucaoPendenteAprovacao()) {
					return true;
				}
			}
		}
		return false; 
	}
	
	private boolean isPossuiAlgumSulacaoAprovada() {
		if (this.metasGerenciar != null) {
			for (GerenciarMetaDTO gmd : this.metasGerenciar) {
				if (gmd.getSelecaoSolucaoAprovadas()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isPossuiAlgumSulacaoRealizada() {
		if (this.metasGerenciar != null) {
			for (GerenciarMetaDTO gmd : this.metasGerenciar) {
				if (gmd.getSelecaoSolucaoRealizadas()) {
					return true;
				}
			}
		}
		return false;
	}


	private Boolean isPossuiTodosSolucoesRealizadas() {
		Set<Entry<Integer, Boolean>> entry = selecaoTodosSolucoesRealizadas
				.entrySet();
		Iterator<Entry<Integer, Boolean>> it = entry.iterator();
		while (it.hasNext()) {
			if (it.next().getValue()) {
				return true;
			}
		}
		return false;
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

	public void resetCamposVisualizacaoColaborador() {
		this.metasGerenciar = new ArrayList<GerenciarMetaDTO>();
		limparSelecaoVisualizacaoColaborador();
	}

	public void resetCampos() {
		metasGerenciar = new ArrayList<GerenciarMetaDTO>();
		this.acao = null;
		limparSelecaoTodos();
	}

	public void limparSelecaoVisualizacaoColaborador() {
		selecaoTodosMetasAprovadas = new HashMap<Integer, Boolean>();
		selecaoTodosSolucoesAprovadas = new HashMap<Integer, Boolean>();
		selecaoTodosSolucoesPendentes = new HashMap<Integer, Boolean>();
		selecaoTodosObservacaoComite = new HashMap<Integer, Boolean>();
		selecaoTodosObservacaoSuperior = new HashMap<Integer, Boolean>();
		selecaoTodosPendenteAprovacao = new HashMap<Integer, Boolean>();
		selecaoTodosSolucoesRealizadas = new HashMap<Integer, Boolean>();
	}

	private void limparSelecaoTodos() {
		limparSelecaoVisualizacaoColaborador();
	}

	public void aprovarMetasSelecionadas() {
		aprovarMetasNomeColaborador();
		pesquisar();
		this.acao = null;
		limparSelecaoTodos();
	}

	public void cancelarMetasSelecionadas() {
		cancelarMetasNomeColaborador();
		pesquisar();
		this.acao = null;
		limparSelecaoTodos();
	}

	public void aprovarSolucaoSelecionadas() {
		aprovarSolucaoEducacional();
		pesquisar();
		this.acao = null;
	}
	
	public void cancelarSolucaoSelecionadas() {
		cancelarSolucaoEducacional();
		pesquisar();
		this.acao = null;
	}

	private void aprovarSolucaoEducacional() {
		try {
			int total = 0;
			int min = getDataTable().getPage() * getDataTable().getRows();
			int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

			if (max > metasGerenciar.size()) {
				max = metasGerenciar.size();
			}

			for (int i = min; i < max; i++) {
				for (GerenciarMetaDTO gmdt : this.metasGerenciar) {
					for (SolucaoEducacionalMeta sm : gmdt.getSolucoesPendenteAprovacao()) {
						if (sm != null) {
							sm.setStatus(StatusSolucaoEducacional.V);
							solucaoEducacionalMetaService.atualizar(sm);
							total++;
						}
					}
				}
			}

			resetCampos();
			FacesUtil.addInfoMessage(total + " solu\u00E7\u00E3o Educacional aprovadas com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}
	
	private void cancelarSolucaoEducacional() {
		try {
			int total = 0;
			int min = getDataTable().getPage() * getDataTable().getRows();
			int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

			if (max > metasGerenciar.size()) {
				max = metasGerenciar.size();
			}

			for (int i = min; i < max; i++) {
				for (GerenciarMetaDTO gmdt : this.metasGerenciar) {
					if (gmdt.getSelecaoSolucaoAprovadas()) {
						for (SolucaoEducacionalMeta sm : gmdt.getSolucoesAprovadas()) {
							if (sm != null) {
								sm.setStatus(StatusSolucaoEducacional.N);
								solucaoEducacionalMetaService.atualizar(sm);
								total++;
							}
						}
					} else if (gmdt.getSelecaoSolucaoPendenteAprovacao()) {
						for (SolucaoEducacionalMeta sm : gmdt.getSolucoesPendenteAprovacao()) {
							if (sm != null) {
								sm.setStatus(StatusSolucaoEducacional.N);
								solucaoEducacionalMetaService.atualizar(sm);
								total++;
							}
						}
					}
				}
			}

			resetCampos();
			FacesUtil.addInfoMessage(total + " solu\u00E7\u00E3o Educacional canceladas com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void cancelarMetasNomeColaborador() {
		try {
			int qtd = 0;
			for (GerenciarMetaDTO mdto : this.metasGerenciar) {
				try {
					if (mdto.getSelecaoAprovadas()) {
						for (Meta m : mdto.getMetasAprovadas()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoCanceladas()) {
						for (Meta m : mdto.getMetasCanceladas()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteAprovacao()) {
						for (Meta m : mdto.getMetasPendenteAprovacao()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteCancelamento()) {
						for (Meta m : mdto.getMetasPendenteCancelamento()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoSuperior()) {
						for (Meta m : mdto.getMetasObservacaoSuperior()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoComite()) {
						for (Meta m : mdto.getMetasObservacaoComite()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							if (m.getTipo() == TipoMeta.E)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
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
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
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
							for (SolucaoEducacionalMeta sm : m.getSolucoesEducacionais()) {
								if (sm != null) {
									sm.setStatus(StatusSolucaoEducacional.V);
									solucaoEducacionalMetaService.atualizar(sm);
								}
							}
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m, 
										TipoConfiguracaoCiclo.DESEMP, 
										TipoGrupo.C);

							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m, 
										TipoConfiguracaoCiclo.DESEMP, 
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoCanceladas()) {
						for (Meta m : mdto.getMetasCanceladas()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteAprovacao()) {
						for (Meta m : mdto.getMetasPendenteAprovacao()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteCancelamento()) {
						for (Meta m : mdto.getMetasPendenteCancelamento()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoSuperior()) {
						for (Meta m : mdto.getMetasObservacaoSuperior()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoComite()) {
						for (Meta m : mdto.getMetasObservacaoComite()) {
							if (m.getTipo() == TipoMeta.I)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.C);
							
							if (m.getTipo() == TipoMeta.E)
								metaService.aprovarMeta(m,
										TipoConfiguracaoCiclo.DESEMP,
										TipoGrupo.E);
							
							if (m.getTipo() == TipoMeta.D)
								metaService.cancelarMeta(m,
										TipoConfiguracaoCiclo.DESENV,
										TipoGrupo.C);
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
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararEnviarObservacaoSelecionadas() {
		try {
			setEnviarObservacao(true);
			setTextoObservacao("");
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void enviarObservacaoMetasSelecionadas() {
		enviarObservacaoNomeColaborador();
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
							metaService.enviarObservacaoSuperiorMeta(m,
									getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoCanceladas()) {
						for (Meta m : mdto.getMetasCanceladas()) {
							metaService.enviarObservacaoSuperiorMeta(m,
									getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteAprovacao()) {
						for (Meta m : mdto.getMetasPendenteAprovacao()) {
							metaService.enviarObservacaoSuperiorMeta(m,
									getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoPendenteCancelamento()) {
						for (Meta m : mdto.getMetasPendenteCancelamento()) {
							metaService.enviarObservacaoSuperiorMeta(m,
									getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoSuperior()) {
						for (Meta m : mdto.getMetasObservacaoSuperior()) {
							metaService.enviarObservacaoSuperiorMeta(m,
									getTextoObservacao());
							qtd++;
						}
					}

					if (mdto.getSelecaoObservacaoComite()) {
						for (Meta m : mdto.getMetasObservacaoComite()) {
							metaService.enviarObservacaoSuperiorMeta(m,
									getTextoObservacao());
							qtd++;
						}
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}
			FacesUtil.addInfoMessage(qtd
					+ " metas envidas observa\u00E7\u00E3o com sucesso!");
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<Unidade> completeUnidade(String descricao) {
		List<Unidade> retorno = unidadeService.findByDescricao(descricao);
		return retorno;
	}

	public List<Usuario> completeColaborador(String nome) {
		List<Usuario> retorno = usuarioService.findAllOrderNome(nome);
		return retorno;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
