package br.com.sebrae.sgm.controller;

import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Indicador;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.IndicadorService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ManterIndicadorBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ManterIndicadorBean.class);

	@Inject
	private IndicadorService indicadorService;

	private Indicador indicador = new Indicador();
	private List<Indicador> listIndicadores;
	private Boolean visualizando = Boolean.FALSE;
	private Boolean editando = Boolean.FALSE;
	private UF uf;
	private String textoJustificativa;
	
	@Override
	public void init() {
		super.init();
		listIndicadores = indicadorService.findAll();
	}

	public String salvarIndicador() {
		try {
			if (this.indicador.getAtivo() == true) {
				this.indicador.setJustificativa(null);
			} else if (this.textoJustificativa == null) {
				FacesUtil.addErrorMessage("O campo justificativas e de preenchimento obrigat\u00F3rio.");
				return "";
			} else {
				this.indicador.setJustificativa(textoJustificativa);
			}
			indicadorService.save(this.indicador);
			indicador = new Indicador();
			textoJustificativa = null;
			listIndicadores = indicadorService.findAll();
			FacesUtil.addInfoMessage("Indicador salvo com sucesso.");
			return "";
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public void editarIndicador(Indicador ind) {
		if(ind.getAtivo() == false){
			ind.setJustificativa(null);
		}
		this.indicador = ind;
	}

	public void visualizarIndicador() {
		try {
			this.visualizando = true;
			this.editando = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void cancelarVisualizacao() {
		this.visualizando = false;
		this.editando = false;
		this.indicador = new Indicador();
	}

	public void excluirIndicador() {
		try {
			this.indicadorService.delete(this.indicador);
			indicador = new Indicador();
			listIndicadores = indicadorService.findAll();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public String getTextoJustificativa() {
		return textoJustificativa;
	}

	public void setTextoJustificativa(String textoJustificativa) {
		this.textoJustificativa = textoJustificativa;
	}

	public List<Indicador> getListIndicadores() {
		return listIndicadores;
	}

	public Indicador getIndicador() {
		return indicador;
	}

	public void setIndicador(Indicador indicador) {
		this.indicador = indicador;
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

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

}
