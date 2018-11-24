package br.com.sebrae.sgm.controller;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.ObjetivoEstrategico;
import br.com.sebrae.sgm.model.Validacao;
import br.com.sebrae.sgm.model.enums.Abrangencia;
import br.com.sebrae.sgm.model.enums.AtivoInativo;
import br.com.sebrae.sgm.service.ObjetivoEstrategicoService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ManterObjetivoEstrategicoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory
			.getLogger(ManterObjetivoEstrategicoBean.class);

	@Inject
	private ObjetivoEstrategicoService objetivoEstrategicoService;

	@Inject
	private AppBean appBean;
	
	private List<ObjetivoEstrategico> listObjetivosEstrategicos;
	private ObjetivoEstrategico objetivoEstrategico = new ObjetivoEstrategico();
	private List<Abrangencia> abrangencias;
	private List<AtivoInativo> listStatus;
	private Boolean visualizando = Boolean.FALSE;
	private Boolean editando = Boolean.FALSE;

	@Override
	public void init() {
		super.init();
		listObjetivosEstrategicos = objetivoEstrategicoService.findAll();
	}
	public List<AtivoInativo> getListStatus() {
		return Arrays.asList(AtivoInativo.values());
	}
	
	public List<Abrangencia> getAbrangencias() {
		return Arrays.asList(Abrangencia.values());
	}

	public String salvarOjetivoEstrategico() {
		try {
			objetivoEstrategicoService.save(this.objetivoEstrategico);
			objetivoEstrategico = new ObjetivoEstrategico();
			listObjetivosEstrategicos = objetivoEstrategicoService.findAll();
			FacesUtil.addInfoMessage("Objetivo estrategico salvo com sucesso.");
			return "";
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}
	
	public void visualizarOjetivoEstrategico() {
		try {
			this.visualizando = true;
			this.editando = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}
	
	
	public void excluirOjetivoEstrategico() {
		try {
			this.objetivoEstrategicoService.delete(this.objetivoEstrategico);
			objetivoEstrategico = new ObjetivoEstrategico();
			listObjetivosEstrategicos = objetivoEstrategicoService.findAll();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}
	
	public void cancelarVisualizacao() {
		this.visualizando = false;
		this.editando = false;
		this.objetivoEstrategico = new ObjetivoEstrategico();
	}

	public List<ObjetivoEstrategico> getListObjetivosEstrategicos() {
		return listObjetivosEstrategicos;
	}

	public ObjetivoEstrategico getObjetivoEstrategico() {
		return objetivoEstrategico;
	}

	public void setObjetivoEstrategico(ObjetivoEstrategico objetivoEstrategico) {
		this.objetivoEstrategico = objetivoEstrategico;
	}

	public Boolean getVisualizando() {
		return visualizando;
	}

	public void setVisualizando(Boolean visualizando) {
		this.visualizando = visualizando;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}
	
}
