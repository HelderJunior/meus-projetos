package br.com.sebrae.sgm.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Categoria;
import br.com.sebrae.sgm.model.FormaAquisicao;
import br.com.sebrae.sgm.model.SolucaoEducacional;
import br.com.sebrae.sgm.model.Validacao;
import br.com.sebrae.sgm.model.enums.Abrangencia;
import br.com.sebrae.sgm.model.enums.AtivoInativo;
import br.com.sebrae.sgm.model.enums.TipoCargaHoraria;
import br.com.sebrae.sgm.service.CategoriaService;
import br.com.sebrae.sgm.service.FormAquisicaoService;
import br.com.sebrae.sgm.service.SolucaoEducacionalService;
import br.com.sebrae.sgm.service.ValidacaoService;
import br.com.sebrae.sgm.utils.FacesUtil;


@SuppressWarnings("unused")
@ConversationScoped
@Named
public class ManterFormaAquisicaoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ManterFormaAquisicaoBean.class);

	@Inject
	private FormAquisicaoService formAquisicaoService;

	@Inject
	private SolucaoEducacionalService solucaoEducacionalService;

	@Inject
	private ValidacaoService validacaoService;

	@Inject
	private CategoriaService categoriaService;

	@Inject
	private AppBean appBean;

	private SolucaoEducacional solucaoEducacional;


	private List<FormaAquisicao> listFormasAquisicoes;
	private FormaAquisicao formaAquisicao = new FormaAquisicao();
	private List<Validacao> validacoes;
	private List<Categoria> categorias;
	private List<Abrangencia> abrangencias;
	private List<AtivoInativo> listStatus;
	private List<TipoCargaHoraria> tipoCargaHorarias;

	private boolean desabilitarTabela=false;
	private boolean desabilitarCampoCargaHoraria=false;
	private boolean desabilitarBotaoSolucaoEducacional=true;
	private boolean editando = Boolean.FALSE;



	@Override
	public void init() {
		super.init();
		listFormasAquisicoes = formAquisicaoService.findAll();
		validacoes = validacaoService.findAll();
		categorias = categoriaService.findAll();
	}

	public List<AtivoInativo> getListStatus() {
		return Arrays.asList(AtivoInativo.values());
	}

	public List<Abrangencia> getAbrangencias() {
		return Arrays.asList(Abrangencia.values());
	}

	public List<TipoCargaHoraria> getTipoCargaHorarias() {
		return Arrays.asList(TipoCargaHoraria.values());
	}

	public String salvarFormaAquisicao() {
		try {
			validarFormaAquisicao();
			if(this.formaAquisicao.getTipoCargaHoraria().equals(TipoCargaHoraria.V)){
				this.formaAquisicao.setCargaHoraria(new BigDecimal(0));
			}
			if (this.formaAquisicao.getTipoCargaHoraria().equals(TipoCargaHoraria.U)){
				this.formaAquisicao.setCargaHoraria(new BigDecimal(0));

				for (SolucaoEducacional listaSelecionada : formaAquisicao.getSolucoesEducacional()) {
					listaSelecionada.setMedidaBase(formaAquisicao.getUnidadeMedida());
				}
			}
			this.formaAquisicao.setUf(appBean.getUfSelecionada());
			formAquisicaoService.save(this.formaAquisicao);
			formaAquisicao = new FormaAquisicao();
			listFormasAquisicoes = formAquisicaoService.findAll();
			FacesUtil.addInfoMessage("Forma de Aquisi\u00E7\u00E3o salva com sucesso.");
			return "";
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public void excluirFormaAquisicao() {
		try {
			this.formAquisicaoService.delete(this.formaAquisicao);
			formaAquisicao = new FormaAquisicao();
			listFormasAquisicoes = formAquisicaoService.findAll();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Esta Forma de aquisi\u00E7\u00E3o n\u00E3o pode ser exclu\u00EDda, pois possui registros associados a ela.");
			log.error(e.getMessage(), e);
		}
	}


	/*
	 * Solucao Educacional
	 */
	public void prepararAdicionarSolucaoEducacional() {
		try {
			solucaoEducacional = new SolucaoEducacional();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void adicionarSolucaoEducacional() {
		boolean status = false;
		try {
			if (formaAquisicao.getSolucoesEducacional() == null) {
				formaAquisicao.setSolucoesEducacional(new ArrayList<SolucaoEducacional>());
			}

			if (editando) {
				for (SolucaoEducacional se : formaAquisicao.getSolucoesEducacional()) {
					if (solucaoEducacional.equals(se)) {
						status = true;
					}
				}
				if (status) {
					formaAquisicao.getSolucoesEducacional().remove(solucaoEducacional);
				}
				editando = false;
			}
			formaAquisicao.getSolucoesEducacional().add(solucaoEducacional);
			solucaoEducacional.setFormaAquisicao(formaAquisicao);
			solucaoEducacional = new SolucaoEducacional();
			FacesUtil.addInfoMessage("Solu\u00E7\u00E3o educacional salva com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editarSolucaoEducacional() {
		this.editando = true;
	}

	public void excluirSolucaoEducacional() {
		try {
			formaAquisicao.getSolucoesEducacional().remove(this.solucaoEducacional);
			solucaoEducacionalService.delete(this.solucaoEducacional);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void validarFormaAquisicao() throws ValidateException {

		List<String> erros = new ArrayList<String>();
		if (this.formaAquisicao.getTipoCargaHoraria().equals(TipoCargaHoraria.U)) {
			if (this.formaAquisicao.getSolucoesEducacional() == null
					|| this.formaAquisicao.getSolucoesEducacional().isEmpty()) {
				erros.add("\u00C9 necess\u00E1rio inserir no m\u00EDnimo uma solu\u00E7\u00E3o educacional para a forma de aquisi\u00E7\u00E3o.");
			}
		}
		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}

	}

	public void ControleExibirComponente(AjaxBehaviorEvent event){

//		TABELA, BOTAO SOLUCOESEDUCACIONAL, IMPUT CARGA HORARIA

		if(event.getComponent().getAttributes().get("value").toString().trim().equalsIgnoreCase("F")){
			desabilitarTabela=false;
			desabilitarCampoCargaHoraria=false;
			desabilitarBotaoSolucaoEducacional=true;

		}
		else if(event.getComponent().getAttributes().get("value").toString().trim().equalsIgnoreCase("V")) {
			desabilitarTabela=false;
			desabilitarCampoCargaHoraria=true;
			desabilitarBotaoSolucaoEducacional=true;

		}
		else {
			desabilitarTabela=true;
			desabilitarCampoCargaHoraria=true;
			desabilitarBotaoSolucaoEducacional=false;
		}

	}

	public void bloquearImputTextCargaHoraria(FormaAquisicao forma){

		if(forma.getTipoCargaHoraria().equals(TipoCargaHoraria.F)){

			desabilitarTabela=false;
			desabilitarCampoCargaHoraria=true;
			desabilitarBotaoSolucaoEducacional=true;

		}
		else if(forma.getTipoCargaHoraria().equals(TipoCargaHoraria.V)) {
			desabilitarTabela=false;
			desabilitarCampoCargaHoraria=false;
			desabilitarBotaoSolucaoEducacional=true;

		}
		else {
			desabilitarTabela=true;
			desabilitarCampoCargaHoraria=true;
			desabilitarBotaoSolucaoEducacional=false;
		}
	}

	public SolucaoEducacional getSolucaoEducacional() {
		return solucaoEducacional;
	}

	public void setSolucaoEducacional(SolucaoEducacional solucaoEducacional) {
		this.solucaoEducacional = solucaoEducacional;
	}

	public FormaAquisicao getFormaAquisicao() {
		return formaAquisicao;
	}

	public void setFormaAquisicao(FormaAquisicao formaAquisicao) {
		this.formaAquisicao = formaAquisicao;
	}

	public List<FormaAquisicao> getListFormasAquisicoes() {
		return listFormasAquisicoes;
	}

	public List<Validacao> getValidacoes() {
		return validacoes;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public boolean isDesabilitarCampoCargaHoraria() {
		return desabilitarCampoCargaHoraria;
	}

	public void setDesabilitarCampoCargaHoraria(boolean desabilitarCampoCargaHoraria) {
		this.desabilitarCampoCargaHoraria = desabilitarCampoCargaHoraria;
	}

	public boolean isdesabilitarTabela() {
		return desabilitarTabela;
	}

	public void setdesabilitarTabela(boolean desabilitarTabela) {
		this.desabilitarTabela = desabilitarTabela;
	}

	public boolean isDesabilitarBotaoSolucaoEducacional() {
		return desabilitarBotaoSolucaoEducacional;
	}

	public void setDesabilitarBotaoSolucaoEducacional(
			boolean desabilitarBotaoSolucaoEducacional) {
		this.desabilitarBotaoSolucaoEducacional = desabilitarBotaoSolucaoEducacional;
	}
}
