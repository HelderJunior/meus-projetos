package br.com.sebrae.sgm.controller;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ManterSolucaoEducacionalBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ManterSolucaoEducacionalBean.class);

	@Inject
	private FormAquisicaoService formAquisicaoService;
	
	@Inject
	private SolucaoEducacionalService solucaoEducacionalService;
	
	@Inject
	private AppBean appBean;
	
	
	private SolucaoEducacional solucaoEducacional = new SolucaoEducacional();
	private List<SolucaoEducacional> listSolucoesEducacionais;
	private List<AtivoInativo> listStatus;

	
	@Override
	public void init() {
		super.init();
		listSolucoesEducacionais = solucaoEducacionalService.findAll();
	}
	
	public List<AtivoInativo> getListStatus() {
		return Arrays.asList(AtivoInativo.values());
	}
	
	public String salvarSolucaoEducacional() {
		try {
			solucaoEducacionalService.save(this.solucaoEducacional);
			listSolucoesEducacionais = solucaoEducacionalService.findAll();
			FacesUtil.addInfoMessage("Solu\u00E7\u00E3o educacional salva com sucesso.");
			return "";
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}
	
	public void excluirSolucaoEducacional() {
		try {
			this.solucaoEducacionalService.delete(this.solucaoEducacional);
			listSolucoesEducacionais = solucaoEducacionalService.findAll();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	
	public List<SolucaoEducacional> getListSolucoesEducacionais() {
		return listSolucoesEducacionais;
	}

	public SolucaoEducacional getSolucaoEducacional() {
		return solucaoEducacional;
	}

	public void setSolucaoEducacional(SolucaoEducacional solucaoEducacional) {
		this.solucaoEducacional = solucaoEducacional;
	}

}
