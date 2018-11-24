package br.com.sebrae.sgm.controller;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Categoria;
import br.com.sebrae.sgm.model.enums.Abrangencia;
import br.com.sebrae.sgm.model.enums.AtivoInativo;
import br.com.sebrae.sgm.service.CategoriaService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ManterCategoriaBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory
			.getLogger(ManterCategoriaBean.class);

	@Inject
	private CategoriaService categoriaService;

	@Inject
	private AppBean appBean;
	
	private List<Categoria> listCategorias;
	private Categoria categoria = new Categoria();
	private List<Abrangencia> abrangencias;
	private List<AtivoInativo> listStatus;
	private Boolean visualizando = Boolean.FALSE;
	private Boolean editando = Boolean.FALSE;

	@Override
	public void init() {
		super.init();
		listCategorias = categoriaService.findAll();
	}
	public List<AtivoInativo> getListStatus() {
		return Arrays.asList(AtivoInativo.values());
	}
	
	public List<Abrangencia> getAbrangencias() {
		return Arrays.asList(Abrangencia.values());
	}

	public String salvarCategoria() {
		try {
			this.categoria.setUf(appBean.getUfSelecionada());
			categoriaService.save(this.categoria);
			categoria = new Categoria();
			listCategorias = categoriaService.findAll();
			FacesUtil.addInfoMessage("Categoria salvo com sucesso.");
			return "";
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}
	
	public void visualizarCategoria() {
		try {
			this.visualizando = true;
			this.editando = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}
	
	public void excluirCategoria() {
		try {
			this.categoriaService.delete(this.categoria);
			categoria = new Categoria();
			listCategorias = categoriaService.findAll();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}
	
	public void cancelarVisualizacao() {
		this.visualizando = false;
		this.editando = false;
		this.categoria = new Categoria();
	}
	
	public List<Categoria> getListCategorias() {
		return listCategorias;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
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
