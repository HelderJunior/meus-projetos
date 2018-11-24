package br.com.sebrae.sgm.controller;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.VisualizacaoMeta;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarExibicaoMetasBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarExibicaoMetasBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;
	
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private Boolean visualizando = Boolean.FALSE;

	private VisualizacaoMeta visualizacaoMeta;

	@Override
	public void init() {
		super.init();
	}

	public String salvar() {
		try {
			this.cicloConfiguracao.setVisualizacaoMeta(this.visualizacaoMeta);
			this.cicloConfiguracao.setStatusConfiguracaoVisualizacaoMetas(StatusConfiguracao.C);
			if(this.getVisualizacaoMeta().getId() == null){
				this.cicloConfiguracao.getVisualizacaoMeta().setId(this.cicloConfiguracao.getId());
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

	public void selecionouSomenteSuasMetas() {
		if (this.getVisualizacaoMeta().getSomenteSuasMetas()) {
			this.getVisualizacaoMeta().setDemaisUnidades(Boolean.FALSE);
			this.getVisualizacaoMeta().setEquipeDemaisUnidades(Boolean.FALSE);
		}
	}

	public void selecionouDemaisUnidades() {
		if (this.getVisualizacaoMeta().getDemaisUnidades()) {
			this.getVisualizacaoMeta().setSomenteSuasMetas(Boolean.FALSE);
			this.getVisualizacaoMeta().setEquipeDemaisUnidades(Boolean.FALSE);
		}
	}
	
	public void selecionouEquipeDemaisUnidades() {
		if (this.getVisualizacaoMeta().getEquipeDemaisUnidades()) {
			//this.getVisualizacaoMeta().setIndivisuaisEquipePertencente(Boolean.FALSE);
			//this.getVisualizacaoMeta().setIndividuaisOutrasUnidades(Boolean.FALSE);
		}
	}
	
	public void selecionouMetasIndividuaisDesempenhoEquipeQualPertence() {
		if (this.getVisualizacaoMeta().getIndivisuaisEquipePertencente()) {
			this.getVisualizacaoMeta().setIndividuaisOutrasUnidades(Boolean.FALSE);
			//this.getVisualizacaoMeta().setEquipeDemaisUnidades(Boolean.FALSE);
		}
	}
	
	public void selecionouMetasIndividuaisOutrasUnidades() {
		if (this.getVisualizacaoMeta().getIndividuaisOutrasUnidades()) {
			this.getVisualizacaoMeta().setIndivisuaisEquipePertencente(Boolean.FALSE);
			//this.getVisualizacaoMeta().setEquipeDemaisUnidades(Boolean.FALSE);
		}
	}
	
	public void selecionouMetasIndividualUnidadesMestaDiretoria() {
		if (this.getVisualizacaoMeta().getIndividualUnidadesMestaDiretoria()) {
			this.getVisualizacaoMeta().setIndividualTodos(Boolean.FALSE);
		}
	}
	
	public void selecionouMetasIndividualTodos() {
		if (this.getVisualizacaoMeta().getIndividualTodos()) {
			this.getVisualizacaoMeta().setIndividualUnidadesMestaDiretoria(Boolean.FALSE);
		}
	}
	
	public void selecionouMetasEquipeMesmaDiretoria() {
		if (this.getVisualizacaoMeta().getEquipeMestaDiretoria()) {
			this.getVisualizacaoMeta().setEquipeTodos(Boolean.FALSE);
		}
	}
	
	public void selecionouMetasEquipeTodos() {
		if (this.getVisualizacaoMeta().getEquipeTodos()) {
			this.getVisualizacaoMeta().setEquipeMestaDiretoria(Boolean.FALSE);
		}
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

	public VisualizacaoMeta getVisualizacaoMeta() {
		if (this.cicloConfiguracao.getVisualizacaoMeta() != null) {
			this.visualizacaoMeta = this.cicloConfiguracao.getVisualizacaoMeta();
		}

		if (this.visualizacaoMeta == null) {
			this.visualizacaoMeta = new VisualizacaoMeta();
		}

		return visualizacaoMeta;
	}

	public void setVisualizacaoMeta(VisualizacaoMeta visualizacaoMeta) {
		this.visualizacaoMeta = visualizacaoMeta;
	}

}
