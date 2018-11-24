package br.com.sebrae.sgm.controller;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Categoria;
import br.com.sebrae.sgm.model.Validacao;
import br.com.sebrae.sgm.model.enums.Abrangencia;
import br.com.sebrae.sgm.model.enums.AtivoInativo;
import br.com.sebrae.sgm.service.ValidacaoService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ManterValidacaoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory
			.getLogger(ManterValidacaoBean.class);

	@Inject
	private ValidacaoService validacaoService;

	@Inject
	private AppBean appBean;
	
	private List<Validacao> listValidacoes;
	private Validacao validacao = new Validacao();
	private List<Abrangencia> abrangencias;
	private List<AtivoInativo> listStatus;
	private Boolean visualizando = Boolean.FALSE;
	private Boolean editando = Boolean.FALSE;

	@Override
	public void init() {
		super.init();
		listValidacoes = validacaoService.findAll();
	}
	
	public List<AtivoInativo> getListStatus() {
		return Arrays.asList(AtivoInativo.values());
	}
	
	public List<Abrangencia> getAbrangencias() {
		return Arrays.asList(Abrangencia.values());
	}

	public String salvarValidacao() {
		try {
			validacaoService.save(this.validacao);
			validacao = new Validacao();
			listValidacoes = validacaoService.findAll();
			FacesUtil.addInfoMessage("Validacao salvo com sucesso.");
			return "";
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}
	
	public void visualizarValidacao() {
		try {
			this.visualizando = true;
			this.editando = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}
	
	
	public void excluirValidacao() {
		try {
			this.validacaoService.delete(this.validacao);
			validacao = new Validacao();
			listValidacoes = validacaoService.findAll();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}
	
	public void cancelarVisualizacao() {
		this.visualizando = false;
		this.editando = false;
		this.validacao = new Validacao();
	}
	
	public Validacao getValidacao() {
		return validacao;
	}

	public void setValidacao(Validacao validacao) {
		this.validacao = validacao;
	}

	public List<Validacao> getListValidacoes() {
		return listValidacoes;
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
