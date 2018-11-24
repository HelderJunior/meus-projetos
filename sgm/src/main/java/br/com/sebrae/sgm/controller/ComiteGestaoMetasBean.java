package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.datatable.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.enums.AcaoComboGestao;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.PropriedadesUsuarioExterno;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ComiteGestaoMetasBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ComiteGestaoMetasBean.class);

	@Inject
	private MetaService metaService;

	@Inject
	private CicloService cicloService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private AppBean appBean;

	private List<Ciclo> ciclos = new ArrayList<Ciclo>();
	private List<Unidade> unidades;
	private Ciclo ciclo;
	private Unidade unidade;
	private Fase faseCiclo;
	private AcaoComboGestao acao;

	private List<Meta> metasGerenciar = new ArrayList<Meta>();

	private Map<Integer, Boolean> selecaoTodos = new HashMap<Integer, Boolean>();

	private DataTable dataTable;

	private String textoObservacao;

	private UF uf;

	public void pesquisar() {
		try {
			validarPesquisa();
			this.metasGerenciar = metaService.findMetasValidacaoComite(this.ciclo, this.faseCiclo, this.unidade,
					this.uf);
			resetarSelecoes();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void validarPesquisa() throws ValidateException {
		List<String> msgErros = new ArrayList<String>();

		if (!informouAlgumFiltro()) {
			msgErros.add("\u00C9 necess\u00E1rio informar no m\u00EDnimo um par\u00E2metro para consulta.");
		}

		if (!msgErros.isEmpty()) {
			throw new ValidateException(msgErros);
		}
	}

	private boolean informouAlgumFiltro() {
		if (ciclo != null || unidade != null || faseCiclo != null || this.uf != null) {
			return true;
		}
		return false;
	}

	private void resetarSelecoes() {
		selecaoTodos = new HashMap<Integer, Boolean>();
		if (metasGerenciar != null) {
			for (Meta m : this.metasGerenciar) {
				m.setSelecionado(false);
			}
		}
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

	public List<Unidade> getUnidades() {
		if (unidades == null || unidades.isEmpty()) {
			unidades = unidadeService.findAll();
		}
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public List<AcaoComboGestao> getAcoesComboGestao() {
		List<AcaoComboGestao> retorno = new ArrayList<AcaoComboGestao>();
		retorno.add(AcaoComboGestao.EO);
		retorno.add(AcaoComboGestao.VM);
		return retorno;
	}

	public Ciclo getCiclo() {
		return ciclo;
	}

	public void setCiclo(Ciclo ciclo) {
		this.ciclo = ciclo;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Fase getFaseCiclo() {
		return faseCiclo;
	}

	public void setFaseCiclo(Fase faseCiclo) {
		this.faseCiclo = faseCiclo;
	}

	public AcaoComboGestao getAcao() {
		return acao;
	}

	public void setAcao(AcaoComboGestao acao) {
		this.acao = acao;
	}

	public List<Meta> getMetasGerenciar() {
		return metasGerenciar;
	}

	public void setMetasGerenciar(List<Meta> metasGerenciar) {
		this.metasGerenciar = metasGerenciar;
	}

	public DataTable getDataTable() {
		if (dataTable == null) {
			dataTable = (DataTable) FacesUtil.getFacesContext().getViewRoot()
					.findComponent("form_listar_metas:tbl_listar_metas");
		}
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	public Boolean getSelecaoTodos() {
		if (selecaoTodos.containsKey(getDataTable().getPage())) {
			return selecaoTodos.get(getDataTable().getPage());
		}
		return Boolean.FALSE;
	}

	public void setSelecaoTodos(Boolean val) {
		selecaoTodos.put(getDataTable().getPage(), val);
	}

	public void alterouSelecaoTodos() {
		try {
			if (getSelecaoTodos()) {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecionado(Boolean.TRUE);
				}
			} else {
				int min = getDataTable().getPage() * getDataTable().getRows();
				int max = (getDataTable().getPage() * getDataTable().getRows()) + getDataTable().getRows();

				if (max > metasGerenciar.size()) {
					max = metasGerenciar.size();
				}

				for (int i = min; i < max; i++) {
					metasGerenciar.get(i).setSelecionado(Boolean.FALSE);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<Fase> getFasesCiclo() {
		return Arrays.asList(Fase.values());
	}

	public String getTextoObservacao() {
		return textoObservacao;
	}

	public void setTextoObservacao(String textoObservacao) {
		this.textoObservacao = textoObservacao;
	}

	public boolean isPossuiItemSelecionado() {
		if (this.metasGerenciar != null) {
			for (Meta meta : this.metasGerenciar) {
				if (meta.getSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}

	public void enviarObservacaoMetasSelecionadas() {
		try {
			List<Meta> metasSucesso = new ArrayList<Meta>();
			try {
				for (Meta m : this.metasGerenciar) {
					if (m.getSelecionado()) {
						metaService.enviarObservacaoComiteMeta(m, this.textoObservacao);
						metasSucesso.add(m);
					}
				}
			} catch (ValidateException ve) {
				FacesUtil.addErrorMessage("frmObservacao", ve.getMsgErrors());
			}
			this.metasGerenciar.removeAll(metasSucesso);
			FacesUtil.addInfoMessage("Observa\u00E7\u00F5es enviadas com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void validarMetasSelecionadas() {
		try {
			List<Meta> metasSucesso = new ArrayList<Meta>();
			try {
				for (Meta m : this.metasGerenciar) {
					if (m.getSelecionado()) {
						if (m.getTipo() == TipoMeta.E)
							metaService.validarMetaComite(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
						else if (m.getTipo() == TipoMeta.I)
							metaService.validarMetaComite(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);

						metasSucesso.add(m);
					}
				}
			} catch (ValidateException ve) {
				FacesUtil.addErrorMessage("frmObservacao", ve.getMsgErrors());
			}
			this.metasGerenciar.removeAll(metasSucesso);
			FacesUtil.addInfoMessage("Metas validadas com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void removerDaLista(Meta meta) {
		this.metasGerenciar.remove(meta);
	}

	public List<Unidade> completeUnidade(String query) {
		if (this.uf != null) {
			List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUF(query, this.uf);
			return retorno;
		} else {
			List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUFs(query, getUfs());
			return retorno;
		}
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public List<UF> getUfs() {
		Set<UF> retorno = new HashSet<UF>();

		List<PropriedadesUsuarioExterno> propriedadesUsuarioExterno = appBean.getUsuarioAutenticado()
				.getPropriedadesUsuarioExterno();
		if (propriedadesUsuarioExterno != null && !propriedadesUsuarioExterno.isEmpty()) {
			for (PropriedadesUsuarioExterno pue : propriedadesUsuarioExterno) {
				retorno.addAll(pue.getUfs());
			}
			return new ArrayList<UF>(retorno);
		}

		return Collections.emptyList();
	}

	public void alterouUf() {
		this.unidade = null;
	}

}
